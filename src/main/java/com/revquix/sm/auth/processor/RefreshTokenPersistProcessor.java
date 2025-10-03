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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revquix.sm.auth.authentication.AuthIdentity;
import com.revquix.sm.auth.repository.RefreshTokenRepository;
import com.revquix.sm.auth.enums.AuthenticationType;
import com.revquix.sm.auth.enums.RefreshTokenStatus;
import com.revquix.sm.auth.enums.UserLoginProvider;
import com.revquix.sm.auth.model.RefreshToken;
import com.revquix.sm.auth.payload.record.RefreshTokenRecord;
import com.revquix.sm.auth.payload.response.FacebookLongLiveTokenResponse;
import com.revquix.sm.auth.payload.response.GoogleRefreshTokenResponse;
import com.revquix.sm.auth.properties.AuthenticationProperties;
import com.revquix.sm.application.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: RefreshTokenPersistProcessor.java
 */

/**
 * RefreshTokenPersistProcessor is responsible for processing and persisting
 * refresh tokens into the database for various authentication methods.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenPersistProcessor {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationProperties authenticationProperties;

    /**
     * Processes and persists a refresh token for local authentication.
     *
     * @param authentication      the authentication object containing user details
     * @param refreshTokenRecord  the refresh token record containing token details
     */
    public void processLocal(Authentication authentication, RefreshTokenRecord refreshTokenRecord) {
        log.info("RefreshTokenPersistProcessor >> process -> {}", refreshTokenRecord.jti());
        AuthIdentity authIdentity = (AuthIdentity) authentication.getPrincipal();
        AuthenticationType authenticationType = authIdentity.getAuthenticationType();
        UserLoginProvider userLoginProvider = null;
        if (authenticationType.equals(AuthenticationType.USER_LOGIN)) {
            userLoginProvider = UserLoginProvider.LOCAL;
        }
        RefreshToken refreshToken = RefreshToken
                .builder()
                .jti(refreshTokenRecord.jti())
                .clientId(authIdentity.getClientId())
                .userId(authIdentity.getUserId())
                .authenticationType(authenticationType)
                .dateGenerated(new Date())
                .expiryDate(DateUtils.addDays(new Date(), authenticationProperties.getJwt().getTokenData().getRefreshTokenExpiryDays()))
                .refreshTokenStatus(RefreshTokenStatus.ACTIVE)
                .userLoginProvider(userLoginProvider)
                .build();
        RefreshToken refreshTokenResponse = refreshTokenRepository.save(refreshToken);
        log.info("RefreshTokenPersistProcessor >> Save -> refreshTokenId: {}, jti: {}", refreshTokenResponse.getRefreshTokenId(), refreshTokenResponse.getJti());
    }

    public void processFacebook(Authentication authentication, RefreshTokenRecord refreshTokenRecord, FacebookLongLiveTokenResponse facebookLongLiveTokenResponse) {
        log.info("RefreshTokenPersistProcessor >> processFacebook -> {}", refreshTokenRecord.jti());
        AuthIdentity authIdentity = (AuthIdentity) authentication.getPrincipal();
        ObjectMapper objectMapper = new ObjectMapper();
        RefreshToken refreshToken = RefreshToken
                .builder()
                .jti(refreshTokenRecord.jti())
                .clientId(authIdentity.getClientId())
                .userId(authIdentity.getUserId())
                .authenticationType(authIdentity.getAuthenticationType())
                .dateGenerated(new Date())
                .expiryDate(DateUtils.addDays(new Date(), authenticationProperties.getJwt().getTokenData().getRefreshTokenExpiryDays()))
                .refreshTokenStatus(RefreshTokenStatus.ACTIVE)
                .ssoTokenData(objectMapper.valueToTree(facebookLongLiveTokenResponse))
                .userLoginProvider(UserLoginProvider.FACEBOOK)
                .build();
        RefreshToken refreshTokenResponse = refreshTokenRepository.save(refreshToken);
        log.info("RefreshTokenPersistProcessor >> Save -> refreshTokenId: {}, jti: {}", refreshTokenResponse.getRefreshTokenId(), refreshTokenResponse.getJti());
    }

    public void processGoogle(Authentication authentication, RefreshTokenRecord refreshTokenRecord, GoogleRefreshTokenResponse googleRefreshTokenResponse) {
        log.info("RefreshTokenPersistProcessor >> processGoogle -> {}", refreshTokenRecord.jti());
        AuthIdentity authIdentity = (AuthIdentity) authentication.getPrincipal();
        ObjectMapper objectMapper = new ObjectMapper();
        RefreshToken refreshToken = RefreshToken
                .builder()
                .jti(refreshTokenRecord.jti())
                .clientId(authIdentity.getClientId())
                .userId(authIdentity.getUserId())
                .authenticationType(authIdentity.getAuthenticationType())
                .dateGenerated(new Date())
                .expiryDate(DateUtils.addDays(new Date(), authenticationProperties.getJwt().getTokenData().getRefreshTokenExpiryDays()))
                .refreshTokenStatus(RefreshTokenStatus.ACTIVE)
                .ssoTokenData(objectMapper.valueToTree(googleRefreshTokenResponse))
                .userLoginProvider(UserLoginProvider.GOOGLE)
                .build();
        RefreshToken refreshTokenResponse = refreshTokenRepository.save(refreshToken);
        log.info("RefreshTokenPersistProcessor >> Save -> refreshTokenId: {}, jti: {}", refreshTokenResponse.getRefreshTokenId(), refreshTokenResponse.getJti());
    }
}
