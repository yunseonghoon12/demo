package com.example.demo.config;

import com.example.demo.login.JwtAuthenticationFilter;
import com.example.demo.login.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class WebFilterConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            @Value("${jwt.cookie-name}") String cookieName) {

        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthenticationFilter(jwtTokenProvider, cookieName));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName("jwtAuthenticationFilter");
        return registration;
    }
}
