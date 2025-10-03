/**
 * Proprietary License Agreement
 * <p>
 * Copyright (c) 2025 Revquix
 * <p>
 * This software is the confidential and proprietary property of Revquix and is provided under a
 * license, not sold. The application owner is Rohit Parihar and Revquix. Only authorized
 * Revquix administrators are permitted to copy, modify, distribute, or sublicense this software
 * under the terms set forth in this agreement.
 * <p>
 * Restrictions
 *
 * You are expressly prohibited from:
 * 1. Copying, modifying, distributing, or sublicensing this software without the express
 *    written permission of Rohit Parihar or Revquix.
 * 2. Reverse engineering, decompiling, disassembling, or otherwise attempting to derive
 *    the source code of the software.
 * 3. Altering or modifying the terms of this license without prior written approval from
 *    Rohit Parihar and Revquix administrators.
 * <p>
 * Disclaimer of Warranties:
 * This software is provided "as is" without any warranties, express or implied. Revquix makes
 * no representations or warranties regarding the software, including but not limited to any
 * warranties of merchantability, fitness for a particular purpose, or non-infringement.
 * <p>
 * For inquiries regarding licensing, please contact: support@Revquix.com.
 */
package com.revquix.sm.auth.config;

/*
  Developer: Rohit Parihar
  Project: ap-auth-provider
  GitHub: github.com/rohit-zip
  File: WebSecurityConfiguration
 */

import com.revquix.sm.application.config.ApplicationMDCFilter;
import com.revquix.sm.auth.authentication.*;
import com.revquix.sm.auth.properties.AuthenticationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

/**
 * WebSecurityConfiguration is a configuration class that sets up web security
 * for the application using JWT authentication. It defines the security filter
 * chain, authentication provider, and authentication manager.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfiguration {

    private final AuthenticationProperties authenticationProperties;
    private final RevquixUserDetailsService revquixUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final RevquixAuthenticationEntryPoint revquixAuthenticationEntryPoint;
    private final RevquixAccessDeniedHandler revquixAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApplicationMDCFilter applicationMDCFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        List<String> paths = authenticationProperties.getExcludedPaths().getJwtPaths().getExcludePaths();
        String[] pathsArray = new String[paths.size()];
        paths.toArray(pathsArray);
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(pathsArray).permitAll()
                            .anyRequest().authenticated();
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(authenticationException -> {
                    authenticationException.authenticationEntryPoint(revquixAuthenticationEntryPoint);
                    authenticationException.accessDeniedHandler(revquixAccessDeniedHandler);
                })
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
        http.addFilterBefore(applicationMDCFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider bloggiosAuthenticationProvider() {
        AuthenticationProvider authenticationProvider = new AuthenticationProvider();
        authenticationProvider.setUserDetailsService(revquixUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> bloggiosAuthenticationProvider().authenticate(authentication);
    }
}
