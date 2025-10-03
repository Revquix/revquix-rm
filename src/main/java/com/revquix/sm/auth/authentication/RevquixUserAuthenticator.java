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
package com.revquix.sm.auth.authentication;

import com.revquix.sm.auth.repository.UserAuthRepository;
import com.revquix.sm.auth.enums.EntrypointType;
import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.application.exception.AuthenticationException;
import com.revquix.sm.auth.model.UserAuth;
import com.revquix.sm.auth.payload.record.LoginCredentials;
import com.revquix.sm.auth.payload.record.LoginPrincipal;
import com.revquix.sm.auth.payload.record.SSOAuthenticationPayload;
import com.revquix.sm.auth.utils.EntrypointTypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: BloggiosUserAuthenticator.java
 */

/**
 * SanaUserAuthenticator is responsible for authenticating users based on various credentials.
 * It interacts with the UserAuthRepository to retrieve user information and validate credentials.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RevquixUserAuthenticator {

    private final RevquixUserDetailsService revquixUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserAuthRepository userAuthRepository;

    public UserAuth authenticateUser(LoginPrincipal loginPrincipal, LoginCredentials loginCredentials) {
        log.info("{} -> authenticateUser (loginPrincipal, loginCredentials)", getClass().getSimpleName());
        EntrypointType entrypointType = EntrypointTypeUtil.parseEntrypoint(loginPrincipal.entrypoint());
        ErrorData errorData;
        if (entrypointType.equals(EntrypointType.email)) errorData = ErrorData.NO_USER_WITH_EMAIL;
        else if (entrypointType.equals(EntrypointType.username)) errorData = ErrorData.NO_USER_WITH_USERNAME;
        else errorData = ErrorData.NO_USER_WITH_MOBILE;
        UserAuth userAuth = userAuthRepository.findByEmailOrUsernameOrMobile(loginPrincipal.entrypoint())
                .orElseThrow(() -> new AuthenticationException(errorData, HttpStatus.UNAUTHORIZED));
        if (!passwordEncoder.matches(loginCredentials.password(), userAuth.getPassword()))
            throw new AuthenticationException(ErrorData.INCORRECT_PASSWORD);
        validateForInactiveUser(userAuth);
        return userAuth;
    }

    public UserAuth authenticateRefreshTokenUser(String userId) {
        log.info("{} -> authenticateRefreshTokenUser", getClass().getSimpleName());
        UserAuth userAuth = userAuthRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException(ErrorData.USER_NOT_FOUND));
        validateForInactiveUser(userAuth);
        return userAuth;
    }

    public void authenticateSSOUser(SSOAuthenticationPayload ssoAuthenticationPayload) {
        log.info("{} -> authenticateSSOUser", getClass().getSimpleName());
        if (Objects.isNull(ssoAuthenticationPayload.userAuth()))
            throw new AuthenticationException(ErrorData.USER_AUTH_NULL_AUTHENTICATE_SSO, HttpStatus.INTERNAL_SERVER_ERROR);
        validateForInactiveUser(ssoAuthenticationPayload.userAuth());
    }

    private static void validateForInactiveUser(UserAuth userDetails) {
        if (!userDetails.getIsEnabled()) {
            throw new AuthenticationException(ErrorData.USER_NOT_ENABLED);
        }
        if (!userDetails.getIsAccountNonLocked()) {
            throw new AuthenticationException(ErrorData.USER_ACCOUNT_LOCKED);
        }
    }
}
