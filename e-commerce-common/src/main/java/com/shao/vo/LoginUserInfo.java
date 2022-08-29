package com.shao.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : SH35856
 * @Description: <h1>通过token解密得到的用户信息</h1>>
 * @date: 2022/8/29 9:53
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginUserInfo {
    /** 用户 id */
    private Long id;

    /** 用户名 */
    private String username;
}
