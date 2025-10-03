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
package com.revquix.sm.auth.processor;

import com.revquix.sm.auth.authentication.AuthenticateHelper;
import com.revquix.sm.auth.authentication.JwtTokenDecoder;
import com.revquix.sm.auth.model.RefreshToken;
import com.revquix.sm.auth.guardrails.RefreshTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: RefreshTokenAuthenticateHelper.java
 */

/**
 * RefreshTokenAuthenticateHelper is responsible for validating a refresh token
 * and generating an authentication token based on it.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenAuthenticateHelper {

    private final JwtTokenDecoder jwtTokenDecoder;
    private final RefreshTokenValidator refreshTokenValidator;
    private final AuthenticateHelper authenticateHelper;

    /**
     * Validates the provided refresh token and generates an authentication token.
     *
     * @param token                  The refresh token to be validated.
     * @param httpServletRequest     The HTTP request context.
     * @return A UsernamePasswordAuthenticationToken if the token is valid.
     * @throws Exception if the token is invalid or any validation fails.
     */
    public UsernamePasswordAuthenticationToken getAuthentication(String token, HttpServletRequest httpServletRequest) {
        log.info("{} >> getAuthentication", getClass().getSimpleName());
        Jwt jwt = jwtTokenDecoder.validateNative(token);
        RefreshToken refreshToken = refreshTokenValidator.validate(jwt, httpServletRequest);
        return authenticateHelper.generateAuthentication(refreshToken);
    }
}
