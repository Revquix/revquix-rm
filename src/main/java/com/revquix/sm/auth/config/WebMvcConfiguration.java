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
  Project: sana-backend
  GitHub: github.com/rohit-zip
  File: WebMvcConfiguration
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * WebMvcConfiguration provides CORS (Cross-Origin Resource Sharing) configuration
 * for the Sana Health Backend application. This configuration allows controlled
 * access from specified origins while maintaining security best practices.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Production-ready CORS configuration</li>
 *   <li>Environment-specific allowed origins</li>
 *   <li>Secure default settings with explicit method and header allowances</li>
 *   <li>Proper credential handling for authenticated requests</li>
 * </ul>
 *
 * <p>Security Considerations:</p>
 * <ul>
 *   <li>Only specified origins are allowed (no wildcard in production)</li>
 *   <li>Limited to specific HTTP methods for security</li>
 *   <li>Explicit header allowances prevent unauthorized access</li>
 *   <li>Credential support is carefully controlled</li>
 * </ul>
 */
@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * Allowed origins for CORS requests. Configurable via application properties.
     * Default includes localhost:2000 for development purposes.
     */
    @Value("${cors.allowed-origins:http://localhost:2000}")
    private String allowedOriginsString;

    /**
     * Maximum age for preflight requests cache in seconds.
     * Default is 3600 seconds (1 hour) for optimal performance.
     */
    @Value("${cors.max-age:3600}")
    private Long maxAge;

    /**
     * Whether to allow credentials in CORS requests.
     * Default is true to support authenticated requests.
     */
    @Value("${cors.allow-credentials:true}")
    private Boolean allowCredentials;

    /**
     * Configures CORS mappings for all endpoints in the application.
     * This method provides a global CORS configuration that applies to all controllers.
     *
     * @param registry the CORS registry to configure
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Configuring CORS with allowed origins: {}", allowedOriginsString);

        registry.addMapping("/**")
                .allowedOrigins(allowedOriginsString.split(","))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders(
                        "Authorization",
                        "Content-Type",
                        "Accept",
                        "Origin",
                        "Access-Control-Request-Method",
                        "Access-Control-Request-Headers",
                        "X-Requested-With",
                        "X-Auth-Token",
                        "X-Client-Version",
                        "User-Agent"
                )
                .exposedHeaders(
                        "Access-Control-Allow-Origin",
                        "Access-Control-Allow-Credentials",
                        "Authorization",
                        "Content-Disposition"
                )
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
    }

    /**
     * Creates a CORS configuration source bean that can be used by Spring Security.
     * This provides a more detailed CORS configuration that integrates seamlessly
     * with the security filter chain.
     *
     * @return configured CorsConfigurationSource for Spring Security integration
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("Creating CORS configuration source for Spring Security integration");

        CorsConfiguration configuration = new CorsConfiguration();

        // Set allowed origins - never use setAllowedOriginPatterns("*") in production
        configuration.setAllowedOrigins(Arrays.asList(allowedOriginsString.split(",")));

        // Allowed HTTP methods for CORS requests
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        // Allowed headers for CORS requests
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "X-Requested-With",
                "X-Auth-Token",
                "X-Client-Version",
                "User-Agent",
                "Cache-Control",
                "Pragma",
                "Expires"
        ));

        // Headers that can be exposed to the client
        configuration.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials",
                "Authorization",
                "Content-Disposition",
                "Content-Length",
                "X-Total-Count"
        ));

        // Allow credentials for authenticated requests
        configuration.setAllowCredentials(allowCredentials);

        // Cache preflight response for better performance
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
