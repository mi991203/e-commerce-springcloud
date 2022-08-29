package com.shao;

import com.shao.util.TokenParseUtil;
import com.shao.vo.LoginUserInfo;

/**
 * @author : SH35856
 * @Description: TODO
 * @date: 2022/8/29 10:59
 */
public class ParseTokenTest {
    public static void main(String[] args) throws Exception {
        final LoginUserInfo loginUserInfo = TokenParseUtil.parseUserInfoFromToken("eyJhbGciOiJSUzI1NiJ9.eyJlLWNvbW1lcmNlLXVzZXIiOiJ7XCJpZFwiOjEwLFwidXNlcm5hbWVcIjpcInNoYW9cIn0iLCJqdGkiOiJlZjEzMzVmMC0yMDJhLTQ3OGItODg2ZC1jMDMxYjg0OGQwYTkiLCJleHAiOjE2NjE3ODg4MDB9.DZ5zA4lViJjHiXtt9PBH-XQ71uLjK_OiZwP_AMEN800GNYYwZzz2aC49kIJMng2CO0xJbMGwFary7pibB2k9mFQBxQf3FNVzlXL1u8vz8BUAIghpw5w1gmM3vbL7T1ugirMuiOtMA4wvnYtcJeI56D1iBmzS-PkkIKT42BaXFZO9q61flUnDyKctoQwd87qIsw1jaosuqfO__aT8m2pt4fGLJqDUzS8ABgUv7o4r_U5QSpV5fxs9I1UtxOiHX6nbk3yd_Ah3CBZertzL-Xvj99eJeqtN_lzMpwVoY4JAC9wnAhKSBEJ_emlXT68N6OoFCA-kiCR0BdX5wjbx8fPRIA");
        System.out.println(loginUserInfo.getUsername() + " " + loginUserInfo.getId());
    }
}
