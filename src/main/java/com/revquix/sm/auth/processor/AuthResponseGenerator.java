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

import com.revquix.sm.auth.authentication.AuthIdentity;
import com.revquix.sm.auth.authentication.JwtTokenGenerator;
import com.revquix.sm.application.constants.ServiceConstants;
import com.revquix.sm.auth.payload.record.RefreshTokenRecord;
import com.revquix.sm.auth.payload.response.AuthResponse;
import com.revquix.sm.auth.payload.response.FacebookLongLiveTokenResponse;
import com.revquix.sm.auth.payload.response.GoogleRefreshTokenResponse;
import com.revquix.sm.auth.properties.AuthenticationProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: AuthResponseGenerator.java
 */

/**
 * AuthResponseGenerator is responsible for generating authentication responses,
 * including access tokens, refresh tokens, and associated cookies.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthResponseGenerator {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final AuthenticationProperties authenticationProperties;
    private final RefreshTokenPersistProcessor refreshTokenPersistProcessor;

    /**
     * Generates an AuthResponse for the given authentication and HTTP request.
     *
     * @param authentication      The authentication object containing user details.
     * @param httpServletRequest  The HTTP request object.
     * @return An AuthResponse containing tokens and user information.
     */
    public AuthResponse generateAuthResponse(Authentication authentication, HttpServletRequest httpServletRequest) {
        log.info("AuthResponseGenerator >> generateAuthResponse");
        AuthenticationProperties.TokenData tokenData = authenticationProperties.getJwt().getTokenData();
        String origin = httpServletRequest.getHeader(ServiceConstants.ORIGIN);
        AuthIdentity authIdentity = (AuthIdentity) authentication.getPrincipal();
        String accessToken = jwtTokenGenerator.generateAccessToken(authentication, httpServletRequest);
        RefreshTokenRecord refreshTokenRecord = jwtTokenGenerator.generateRefreshToken(authentication, httpServletRequest);
        ResponseCookie authResponseCookie = getAuthResponseCookie(refreshTokenRecord.token(), origin);
        AuthResponse authResponse = getAuthResponse(accessToken, authIdentity, origin, tokenData, authResponseCookie);
        refreshTokenPersistProcessor.processLocal(authentication, refreshTokenRecord);
        return authResponse;
    }

    /**
     * Generates an AuthResponse for Facebook authentication.
     *
     * @param authentication                The authentication object containing user details.
     * @param httpServletRequest            The HTTP request object.
     * @param facebookLongLiveTokenResponse The Facebook long-lived token response.
     * @return An AuthResponse containing tokens and user information.
     */
    public AuthResponse generateFacebookAuthResponse(Authentication authentication, HttpServletRequest httpServletRequest, FacebookLongLiveTokenResponse facebookLongLiveTokenResponse) {
        log.info("AuthResponseGenerator >> generateFacebookAuthResponse");
        AuthenticationProperties.TokenData tokenData = authenticationProperties.getJwt().getTokenData();
        String origin = httpServletRequest.getHeader(ServiceConstants.ORIGIN);
        AuthIdentity authIdentity = (AuthIdentity) authentication.getPrincipal();
        String accessToken = jwtTokenGenerator.generateAccessToken(authentication, httpServletRequest);
        RefreshTokenRecord refreshTokenRecord = jwtTokenGenerator.generateRefreshToken(authentication, httpServletRequest);
        ResponseCookie authResponseCookie = getAuthResponseCookie(refreshTokenRecord.token(), origin);
        AuthResponse authResponse = getAuthResponse(accessToken, authIdentity, origin, tokenData, authResponseCookie);
        refreshTokenPersistProcessor.processFacebook(authentication, refreshTokenRecord, facebookLongLiveTokenResponse);
        return authResponse;
    }

    /**
     * Generates an AuthResponse for Google authentication.
     *
     * @param authentication               The authentication object containing user details.
     * @param httpServletRequest           The HTTP request object.
     * @param googleRefreshTokenResponse   The Google refresh token response.
     * @return An AuthResponse containing tokens and user information.
     */
    public AuthResponse generateGoogleAuthResponse(Authentication authentication, HttpServletRequest httpServletRequest, GoogleRefreshTokenResponse googleRefreshTokenResponse) {
        log.info("AuthResponseGenerator >> generateGoogleAuthResponse");
        AuthenticationProperties.TokenData tokenData = authenticationProperties.getJwt().getTokenData();
        String origin = httpServletRequest.getHeader(ServiceConstants.ORIGIN);
        AuthIdentity authIdentity = (AuthIdentity) authentication.getPrincipal();
        String accessToken = jwtTokenGenerator.generateAccessToken(authentication, httpServletRequest);
        RefreshTokenRecord refreshTokenRecord = jwtTokenGenerator.generateRefreshToken(authentication, httpServletRequest);
        ResponseCookie authResponseCookie = getAuthResponseCookie(refreshTokenRecord.token(), origin);
        AuthResponse authResponse = getAuthResponse(accessToken, authIdentity, origin, tokenData, authResponseCookie);
        refreshTokenPersistProcessor.processGoogle(authentication, refreshTokenRecord, googleRefreshTokenResponse);
        return authResponse;
    }

    // Helper method to construct AuthResponse
    private static AuthResponse getAuthResponse(String accessToken, AuthIdentity authIdentity, String origin, AuthenticationProperties.TokenData tokenData, ResponseCookie authResponseCookie) {
        return AuthResponse
                .builder()
                .accessToken(accessToken)
                .authenticationType(authIdentity.getAuthenticationType())
                .expiresIn(Objects.isNull(origin) ? tokenData.getLongAccessTokenExpiryMinutes() + " minutes" : tokenData.getAccessTokenExpiryMinutes() + " minutes")
                .isLongToken(Objects.isNull(origin))
                .userId(authIdentity.getUserId())
                .email(authIdentity.getEmail())
                .username(authIdentity.getUsername())
                .clientId(authIdentity.getClientId())
                .refreshTokenCookie(authResponseCookie)
                .scopes(authIdentity.getScopes())
                .roles(authIdentity.getRoles())
                .hasPassword(authIdentity.isHasPassword())
                .providers(authIdentity.getProviders())
                .lastPasswordChange(authIdentity.getLastPasswordChange())
                .build();
    }

    // Helper method to create the authentication response cookie
    private ResponseCookie getAuthResponseCookie(String refreshToken, String origin) {
        log.info("AuthResponseGenerator >> getAuthResponseCookie -> Generating Auth Response Cookie");
        AuthenticationProperties.TokenData tokenData = authenticationProperties.getJwt().getTokenData();
        boolean isHttpOnly = tokenData.getIsHttpOnly();
        if (StringUtils.hasText(origin)) {
            isHttpOnly = !origin.contains("localhost:");
        }
        return ResponseCookie
                .from(tokenData.getRefreshTokenCookieName(), refreshToken)
                .httpOnly(isHttpOnly)
                .maxAge(tokenData.getRefreshTokenExpiryDays() * 86400L)
                .path("/")
                .sameSite(tokenData.getSameSite())
                .secure(tokenData.getSecure())
                .build();
    }
}
