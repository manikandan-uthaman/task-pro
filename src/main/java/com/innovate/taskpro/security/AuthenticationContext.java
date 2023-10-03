package com.innovate.taskpro.security;

import com.innovate.taskpro.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Collections;

@Component
public class AuthenticationContext {

    private Environment environment;

    @Autowired
    private SecurityUtil securityUtil;

    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public AuthenticationContext(Environment environment) {
        this.environment = environment;
    }

    public boolean isEnabled() {
        return Arrays.asList(environment.getActiveProfiles()).contains("security");
    }

    public boolean isDisabled() {
        return !isEnabled();
    }

    public void setSecurityContextHolder(UserDto userDetails, String token) {
        logger.debug("Set user in security contest");
        AbstractAuthenticationToken principal = new TokenBasedAuthenticationToken(userDetails, token, Collections.emptyMap());
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        SecurityContextHolder.getContext().setAuthentication(principal);
    }

    public String getUserIdFromContext() {
        UserDto userDetails = securityUtil.getUserFromContext();
        return userDetails.getUserId();
    }
}
