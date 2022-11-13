package com.shao.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shao.account.BalanceInfo;
import com.shao.common.EcommerceBalance;
import com.shao.filter.AccessContext;
import com.shao.mapper.EcommerceBalanceMapper;
import com.shao.service.IBalanceService;
import com.shao.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <h1>用于余额相关服务接口实现</h1>
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BalanceServiceImpl implements IBalanceService {

    @Resource
    private EcommerceBalanceMapper balanceMapper;

    @Override
    public BalanceInfo getCurrentUserBalanceInfo() {

        LoginUserInfo loginUserInfo = AccessContext.getLoginUserInfo();
        BalanceInfo balanceInfo = new BalanceInfo(
                loginUserInfo.getId(), 0L
        );

        EcommerceBalance ecommerceBalance =
                balanceMapper.selectOne(new QueryWrapper<EcommerceBalance>().eq("user_id", loginUserInfo.getId()));
        if (null != ecommerceBalance) {
            balanceInfo.setBalance(ecommerceBalance.getBalance());
        } else {
            // 如果还没有用户余额记录, 这里创建出来，余额设定为0即可
            EcommerceBalance newBalance = new EcommerceBalance();
            newBalance.setUserId(loginUserInfo.getId());
            newBalance.setBalance(0L);
            balanceMapper.insert(newBalance);
            log.info("init user balance record: [{}]", newBalance.getId());
        }

        return balanceInfo;
    }

    @Override
    public BalanceInfo deductBalance(BalanceInfo balanceInfo) {

        LoginUserInfo loginUserInfo = AccessContext.getLoginUserInfo();

        // 扣减用户余额的一个基本原则: 扣减额 <= 当前用户余额
        EcommerceBalance ecommerceBalance =
                balanceMapper.selectOne(new QueryWrapper<EcommerceBalance>().eq("user_id", loginUserInfo.getId()));
        if (null == ecommerceBalance
                || ecommerceBalance.getBalance() - balanceInfo.getBalance() < 0
        ) {
            throw new RuntimeException("user balance is not enough!");
        }

        Long sourceBalance = ecommerceBalance.getBalance();
        ecommerceBalance.setBalance(ecommerceBalance.getBalance() - balanceInfo.getBalance());
        balanceMapper.insert(ecommerceBalance);
        log.info("deduct balance: [{}], [{}], [{}]",
                ecommerceBalance.getId(), sourceBalance,
                balanceInfo.getBalance());

        return new BalanceInfo(
                ecommerceBalance.getUserId(),
                ecommerceBalance.getBalance()
        );
    }
}
