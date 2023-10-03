package com.innovate.taskpro.security;

import com.innovate.taskpro.UserDto;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Map;

public class TokenBasedAuthenticationToken extends AbstractAuthenticationToken {

    private transient UserDto principal;
    private transient String securityToken;
    private transient Map<String, Object> additionalInformation;

    public TokenBasedAuthenticationToken(UserDto principal, String securityToken, Map<String, Object> additionalInformation) {
        super(principal.getAuthorities());
        this.principal = principal;
        this.securityToken = securityToken;
        this.additionalInformation = additionalInformation;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
