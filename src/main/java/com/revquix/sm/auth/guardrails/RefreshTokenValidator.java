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
package com.revquix.sm.auth.guardrails;

import com.revquix.sm.auth.authentication.JwtTokenDecoder;
import com.revquix.sm.auth.repository.RefreshTokenRepository;
import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.application.exception.AuthenticationException;
import com.revquix.sm.auth.model.RefreshToken;
import com.revquix.sm.application.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: RefreshTokenValidator.java
 */

/**
 * RefreshTokenValidator is responsible for validating refresh tokens.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenValidator {

    private final JwtTokenDecoder jwtTokenDecoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final IpUtils ipUtils;

    /**
     * Validates the provided JWT refresh token.
     *
     * @param jwt The JWT to be validated.
     * @param httpServletRequest The HTTP request containing the token.
     * @return The corresponding RefreshToken if valid.
     * @throws AuthenticationException if the token is invalid or any validation fails.
     */
    public RefreshToken validate(Jwt jwt, HttpServletRequest httpServletRequest) {
        log.info("{} >> validate", getClass().getSimpleName());
        String jti = jwtTokenDecoder.extractJti(jwt);
        RefreshToken refreshToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new AuthenticationException(ErrorData.REFRESH_TOKEN_EXCEPTION));
        log.info("{} >> validate -> Deleting Refresh Token", getClass().getSimpleName());
        String remoteAddress = jwtTokenDecoder.extractRemoteAddress(jwt);
        String currentAddress = ipUtils.getRemoteAddress();
        if (!remoteAddress.equals(currentAddress)) {
            throw new AuthenticationException(ErrorData.REMOTE_ADDRESS_AUTHENTICATION_FAILED);
        }
        refreshTokenRepository.delete(refreshToken);
        return refreshToken;
    }
}
