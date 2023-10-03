package com.innovate.taskpro.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovate.taskpro.UserDto;
import com.innovate.taskpro.entity.UserBO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private AuthenticationEntryPoint authenticationEntryPoint;
    private AuthenticationContext authenticationContext;
    private SecurityUtil securityUtil;

    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public TokenAuthenticationFilter(AuthenticationEntryPoint authenticationEntryPoint,
                                     AuthenticationContext authenticationContext, SecurityUtil securityUtil) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationContext = authenticationContext;
        this.securityUtil = securityUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (authenticationContext.isDisabled()) {
            logger.debug("Authentication disabled. Entering mock mode..");
            ObjectMapper obj = new ObjectMapper();
            String file = ClassLoader.getSystemClassLoader().getResource("mockUser.json").getFile();
            UserBO userDetails = obj.readValue(new File(file), UserBO.class);
            logger.debug("Populate security context with mock user");
            authenticationContext.setSecurityContextHolder(UserDto.build(userDetails), null);
            filterChain.doFilter(request, response);
        } else {
            try {
                String token = securityUtil.parseToken(request);
                if (!StringUtils.hasLength(token)) {
                    logger.error("Token not found");
                    throw new AuthenticationCredentialsNotFoundException("Authentication Failed: Token not found");
                }
                doAuthentication(token);
                filterChain.doFilter(request, response);
            } catch (AuthenticationException ex)  {
                logger.error("Authentication Exception");
                authenticationEntryPoint.commence(request, response, ex);
            }
        }
    }

    private void doAuthentication(String token) {
        boolean isValidToken = securityUtil.validateToken(token);
        if (!isValidToken) {
            logger.error("Token validation failed");
            return;
        }
        UserDto userDetails = securityUtil.getUserFromToken(token);
        authenticationContext.setSecurityContextHolder(userDetails, token);
    }
}
