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
package com.revquix.sm.auth.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: AuthenticationProperties.java
 */

/**
 * AuthenticationProperties is a configuration properties class that holds
 * authentication-related settings, including JWT, excluded paths, and OAuth providers.
 */
@ConfigurationProperties(
        prefix = "bloggios-auth"
)
@Configuration
@Getter
@Setter
public class AuthenticationProperties {

    private JwtData jwt;
    private ExcludedPaths excludedPaths;
    private Oauth oauth;

    @Getter
    @Setter
    public static class JwtData {
        private KeyProvider keyProvider;
        private TokenData tokenData;
    }

    @Getter
    @Setter
    public static class KeyProvider {
        private String keyStorePath;
        private String keyStorePassword;
        private String keyAlias;
    }

    @Getter
    @Setter
    public static class TokenData {
        private int accessTokenExpiryMinutes;
        private int refreshTokenExpiryDays;
        private int longAccessTokenExpiryMinutes;
        private String refreshTokenCookieName;
        private Boolean isHttpOnly;
        private String sameSite;
        private Boolean secure;
        private Boolean isRemoteAddressAuthentication;
    }

    @Getter
    @Setter
    public static class ExcludedPaths {
        private JwtPaths jwtPaths;
        private BasicPaths basicPaths;
    }

    @Getter
    @Setter
    public static class JwtPaths {
        private List<String> excludePaths = new ArrayList<>();
        private List<String> clientAuthenticationPaths = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class BasicPaths {
        private List<String> paths = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Oauth {
        private boolean ssoEnabled;
        private List<String> providers;
        private Client clients;
    }

    @Getter
    @Setter
    public static class Client {
        private Google google;
        private Facebook facebook;
    }

    @Getter
    @Setter
    public static class Google {
        private String clientId;
        private String clientSecret;
    }

    @Getter
    @Setter
    public static class Facebook {
        private String appId;
        private String appSecret;
    }
}
