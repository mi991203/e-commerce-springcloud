package com.shao.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : SH35856
 * @Description: <h1>用户登录信息校验</h1>
 * @date: 2022/8/29 9:54
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsernameAndPassword {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;
}
