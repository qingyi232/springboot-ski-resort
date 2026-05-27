package com.ski.resort.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（因为使用 JWT）
            .csrf().disable()
            
            // 禁用 CORS（开发环境）
            .cors().disable()
            
            // 配置 Session 为无状态
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            
            .and()
            // 配置访问权限 - 使用 authorizeRequests 而不是 authorizeHttpRequests
            .authorizeRequests()
                // 放行所有请求（暂时）
                .anyRequest().permitAll()
            
            .and()
            // 禁用默认登录页
            .formLogin().disable()
            .httpBasic().disable();

        return http.build();
    }
}

