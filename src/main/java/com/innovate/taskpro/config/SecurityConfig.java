package com.innovate.taskpro.config;

import com.innovate.taskpro.security.AuthenticationContext;
import com.innovate.taskpro.security.RestrictAccessWithoutTokenEntryPoint;
import com.innovate.taskpro.security.SecurityUtil;
import com.innovate.taskpro.security.TokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class SecurityConfig {

    @Autowired
    private AuthenticationContext authenticationContext;

    @Autowired
    private SecurityUtil securityUtil;

    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new RestrictAccessWithoutTokenEntryPoint();
    }

    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(authenticationEntryPoint(), authenticationContext, securityUtil);
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint()))
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers(mvc.pattern("/**")).permitAll()
                                        .anyRequest().authenticated());
        return http.build();
    }


}
