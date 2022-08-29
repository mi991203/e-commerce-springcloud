package com.shao.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shao.constant.AuthorityConstant;
import com.shao.constant.CommonConstant;
import com.shao.entity.TEcommerceUser;
import com.shao.mapper.TEcommerceUserMapper;
import com.shao.service.IJWTService;
import com.shao.vo.LoginUserInfo;
import com.shao.vo.UsernameAndPassword;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * @author : SH35856
 * @Description: <h1>JWT 相关服务接口实现</h1>
 * @date: 2022/8/29 10:01
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class JWTServiceImpl implements IJWTService {
    @Resource
    private TEcommerceUserMapper tEcommerceUserMapper;

    @Override
    public String generateToken(String username, String password) throws Exception {
        return generateToken(username, password, -1);
    }

    @Override
    public String generateToken(String username, String password, int expire) throws Exception {
        // 首先需要验证用户是否能够通过授权校验, 即输入的用户名和密码能否匹配数据表记录
        TEcommerceUser tEcommerceUser = tEcommerceUserMapper.selectOne(
                new QueryWrapper<TEcommerceUser>().eq("username", username)
                        .eq("password", password)
        );
        if (null == tEcommerceUser) {
            log.error("can not find user: [{}], [{}]", username, password);
            return null;
        }

        // Token 中塞入对象, 即 JWT 中存储的信息, 后端拿到这些信息就可以知道是哪个用户在操作
        LoginUserInfo loginUserInfo = new LoginUserInfo(
                tEcommerceUser.getId(), tEcommerceUser.getUsername()
        );

        if (expire <= 0) {
            expire = AuthorityConstant.DEFAULT_EXPIRE_DAY;
        }

        // 计算超时时间
        ZonedDateTime zdt = LocalDate.now().plus(expire, ChronoUnit.DAYS)
                .atStartOfDay(ZoneId.systemDefault());
        Date expireDate = Date.from(zdt.toInstant());

        return Jwts.builder()
                // jwt payload --> KV
                .claim(CommonConstant.JWT_USER_INFO_KEY, JSON.toJSONString(loginUserInfo))
                // jwt id
                .setId(UUID.randomUUID().toString())
                // jwt 过期时间
                .setExpiration(expireDate)
                // jwt 签名 --> 加密
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public String registerUserAndGenerateToken(UsernameAndPassword usernameAndPassword) throws Exception {
        final TEcommerceUser tEcommerceUser = tEcommerceUserMapper.selectOne(new QueryWrapper<TEcommerceUser>().eq("username", usernameAndPassword.getUsername()));
        if (null != tEcommerceUser) {
            log.info("{}用户已经创建", usernameAndPassword.getUsername());
            return null;
        }
        final TEcommerceUser newTEcommerceUser = new TEcommerceUser();
        newTEcommerceUser.setUsername(usernameAndPassword.getUsername());
        newTEcommerceUser.setPassword(usernameAndPassword.getPassword());
        newTEcommerceUser.setExtraInfo("{}");
        tEcommerceUserMapper.insert(newTEcommerceUser);

        return generateToken(newTEcommerceUser.getUsername(), newTEcommerceUser.getPassword());
    }


    /**
     * <h2>根据本地存储的私钥获取到 PrivateKey 对象</h2>
     * */
    private PrivateKey getPrivateKey() throws Exception {

        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                new BASE64Decoder().decodeBuffer(AuthorityConstant.PRIVATE_KEY));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(priPKCS8);
    }
}
