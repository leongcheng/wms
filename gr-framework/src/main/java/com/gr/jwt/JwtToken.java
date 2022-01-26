package com.gr.jwt;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author liangc
 * @date 2020/12/10 10:18
 */
public class JwtToken implements AuthenticationToken {
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
