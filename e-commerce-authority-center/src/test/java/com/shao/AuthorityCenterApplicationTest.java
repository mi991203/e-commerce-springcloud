package com.shao;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.shao.entity.TEcommerceUser;
import com.shao.service.ITEcommerceUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class AuthorityCenterApplicationTest {
    @Resource
    private ITEcommerceUserService itEcommerceUserServicel;

    @Test
    public void contextLoad() {

    }

    @Test
    public void saveUser() {
        TEcommerceUser ecommerceUser = new TEcommerceUser();
        ecommerceUser.setUsername("ImoocQinyi@imooc.com");
        ecommerceUser.setPassword(MD5.create().digestHex("12345678"));
        ecommerceUser.setExtraInfo("{}");
        log.info("save user: [{}]",
                JSON.toJSON(itEcommerceUserServicel.save(ecommerceUser)));


    }
}
