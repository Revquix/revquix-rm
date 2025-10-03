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

/*
  Developer: Rohit Parihar
  Project: bloggios-auth-server
  GitHub: github.com/rohit-zip
  File: LogoutUserProcessor
 */

import com.revquix.sm.auth.authentication.JwtTokenDecoder;
import com.revquix.sm.auth.repository.RefreshTokenRepository;
import com.revquix.sm.auth.model.RefreshToken;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

/**
 * LogoutUserProcessor is responsible for processing user logout by
 * validating and deleting the associated refresh token from the database.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LogoutUserProcessor {

    private final JwtDecoder jwtDecoder;
    private final JwtTokenDecoder jwtTokenDecoder;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Processes user logout by validating the provided refresh token cookie
     * and deleting the corresponding refresh token from the database.
     *
     * @param optionalCookie An Optional containing the refresh token cookie.
     */
    public void process(Optional<Cookie> optionalCookie) {
        if (optionalCookie.isEmpty()) return;
        String refreshToken = optionalCookie.get().getValue();
        try {
            Jwt jwt = jwtDecoder.decode(refreshToken);
            String jti = jwtTokenDecoder.extractJti(jwt);
            Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByJti(jti);
            if (refreshTokenOptional.isEmpty()) {
                log.warn("{} >> process -> Refresh Token data is already empty", getClass().getSimpleName());
            }
            RefreshToken response = refreshTokenOptional.get();
            refreshTokenRepository.delete(response);
        } catch (JwtValidationException exception) {
            Collection<OAuth2Error> errors = exception.getErrors();
            for (OAuth2Error error : errors) {
                log.error("{} >> process -> Refresh Token validation error while logout, error: {}", error.getDescription(), error);
            }
        }
    }
}
