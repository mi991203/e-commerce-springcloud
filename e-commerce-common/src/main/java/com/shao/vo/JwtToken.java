package com.shao.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : SH35856
 * @Description: <h1>授权中心获取到的token</h1>
 * @date: 2022/8/29 9:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtToken {

    /** JWT */
    private String token;
}
