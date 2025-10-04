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
package com.revquix.sm.auth.service.impl;

import com.revquix.sm.auth.authentication.AuthenticateHelper;
import com.revquix.sm.auth.authentication.RevquixFacebookOAuthInitiator;
import com.revquix.sm.auth.authentication.RevquixGoogleOAuthInitiator;
import com.revquix.sm.auth.enums.AuthProvider;
import com.revquix.sm.auth.enums.AuthenticationType;
import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.application.exception.AuthenticationException;
import com.revquix.sm.auth.model.UserAuth;
import com.revquix.sm.auth.payload.record.LoginCredentials;
import com.revquix.sm.auth.payload.record.LoginPrincipal;
import com.revquix.sm.auth.payload.record.SSOAuthenticationPayload;
import com.revquix.sm.auth.payload.request.AuthenticateRequest;
import com.revquix.sm.auth.payload.request.FacebookOauthRequest;
import com.revquix.sm.auth.payload.request.GoogleOAuthRequest;
import com.revquix.sm.auth.payload.response.*;
import com.revquix.sm.auth.processor.*;
import com.revquix.sm.auth.properties.AuthenticationProperties;
import com.revquix.sm.auth.service.AuthenticationService;
import com.revquix.sm.auth.utils.CookieUtils;
import com.revquix.sm.auth.guardrails.AuthenticateValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: AuthenticationServiceImplementation.java
 */

/**
 * Implementation of the AuthenticationService interface.
 * This service handles user authentication, token refresh, social OAuth logins,
 * and logout functionality.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticateValidator authenticateValidator;
    private final AuthenticationManager authenticationManager;
    private final AuthResponseGenerator authResponseGenerator;
    private final AuthenticationProperties authenticationProperties;
    private final RefreshTokenAuthenticateHelper refreshTokenAuthenticateHelper;
    private final RevquixFacebookOAuthInitiator revquixFacebookOAuthInitiator;
    private final RevquixGoogleOAuthInitiator revquixGoogleOAuthInitiator;
    private final FacebookRegisterUser facebookRegisterUser;
    private final AuthenticateHelper authenticateHelper;
    private final GoogleRegisterUser googleRegisterUser;
    private final LogoutUserProcessor logoutUserProcessor;
    private final LoginHistoryProcessor loginHistoryProcessor;

    /**
     * Authenticates a user based on the provided authentication request and HTTP request.
     *
     * @param authenticateRequest The authentication request containing user credentials.
     * @param httpServletRequest  The HTTP request object.
     * @return A ResponseEntity containing the AuthResponse with tokens and user information.
     */
    @Override
    public ResponseEntity<AuthResponse> authenticate(AuthenticateRequest authenticateRequest, HttpServletRequest httpServletRequest) {
        log.info("{} >> authenticate", getClass().getSimpleName());
        authenticateValidator.validateAuthenticateRequest(authenticateRequest);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        new LoginPrincipal(authenticateRequest.getEntrypoint(), authenticateRequest.getClientId()),
                        new LoginCredentials(authenticateRequest.getPassword(), authenticateRequest.getClientSecret())
                )
        );
        authenticateValidator.validateOrigins(authentication, httpServletRequest);
        AuthResponse authResponse = authResponseGenerator.generateAuthResponse(authentication, httpServletRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (authResponse.getAuthenticationType().equals(AuthenticationType.USER_LOGIN)) {
            loginHistoryProcessor.process(authResponse, AuthProvider.email);
            return ResponseEntity.accepted().header(HttpHeaders.SET_COOKIE, authResponse.getRefreshTokenCookie().toString()).body(authResponse);
        } else {
            return ResponseEntity.accepted().body(authResponse);
        }
    }

    /**
     * Refreshes the authentication token using the refresh token from cookies.
     *
     * @param httpServletRequest  The HTTP request object.
     * @param httpServletResponse The HTTP response object.
     * @return A ResponseEntity containing the AuthResponse with new tokens and user information.
     */
    @Override
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.info("{} >> refreshToken", getClass().getSimpleName());
        String cookieName = authenticationProperties.getJwt().getTokenData().getRefreshTokenCookieName();
        Cookie[] cookies = httpServletRequest.getCookies();
        if (Objects.isNull(cookies)) throw new AuthenticationException(ErrorData.NOT_LOGGED_IN);
        String refreshToken = CookieUtils.getCookie(httpServletRequest, cookieName)
                .orElseThrow(() -> new AuthenticationException(ErrorData.NOT_LOGGED_IN)).getValue();
        UsernamePasswordAuthenticationToken authentication = refreshTokenAuthenticateHelper.getAuthentication(refreshToken, httpServletRequest);
        authenticateValidator.validateOrigins(authentication, httpServletRequest);
        AuthResponse authResponse = authResponseGenerator.generateAuthResponse(authentication, httpServletRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.accepted().header(HttpHeaders.SET_COOKIE, authResponse.getRefreshTokenCookie().toString()).body(authResponse);
    }

    /**
     * Handles Facebook OAuth authentication.
     *
     * @param facebookOauthRequest The Facebook OAuth request containing the access token and client details.
     * @param httpServletRequest   The HTTP request object.
     * @return A ResponseEntity containing the AuthResponse with tokens and user information.
     */
    @Override
    public ResponseEntity<AuthResponse> facebookOauth(FacebookOauthRequest facebookOauthRequest, HttpServletRequest httpServletRequest) {
        log.info("{} >> facebookOauth", getClass().getSimpleName());
        boolean validAccessToken = revquixFacebookOAuthInitiator.isValidAccessToken(facebookOauthRequest);
        if (!validAccessToken) {
            throw new AuthenticationException(ErrorData.INVALID_FACEBOOK_ACCESS_TOKEN);
        }
        FacebookLongLiveTokenResponse facebookLongLiveTokenResponse = revquixFacebookOAuthInitiator.exchangeLongLiveToken(facebookOauthRequest);
        FacebookUserDetailsResponse facebookUserDetails = revquixFacebookOAuthInitiator.getUserDetails(facebookLongLiveTokenResponse.getAccessToken());
        UserAuth userAuth = facebookRegisterUser.process(facebookUserDetails);
        UsernamePasswordAuthenticationToken authentication = authenticateHelper.generateSSOAuthentication(new SSOAuthenticationPayload(
                userAuth,
                facebookOauthRequest.getClientId(),
                facebookOauthRequest.getClientSecret(),
                AuthProvider.facebook
        ));
        AuthResponse authResponse = authResponseGenerator.generateFacebookAuthResponse(authentication, httpServletRequest, facebookLongLiveTokenResponse);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        loginHistoryProcessor.process(authResponse, AuthProvider.email);
        return ResponseEntity.accepted().header(HttpHeaders.SET_COOKIE, authResponse.getRefreshTokenCookie().toString()).body(authResponse);
    }

    /**
     * Handles Google OAuth authentication.
     *
     * @param googleOAuthRequest  The Google OAuth request containing the authorization code and client details.
     * @param httpServletRequest  The HTTP request object.
     * @return A ResponseEntity containing the AuthResponse with tokens and user information.
     */
    @Override
    public ResponseEntity<AuthResponse> googleOauth(GoogleOAuthRequest googleOAuthRequest, HttpServletRequest httpServletRequest) {
        GoogleTokenInfoResponse googleTokenInfoResponse = revquixGoogleOAuthInitiator.getTokenInfo(googleOAuthRequest.getAccessToken());
        UserAuth userAuth = googleRegisterUser.process(googleTokenInfoResponse);
        UsernamePasswordAuthenticationToken authentication = authenticateHelper.generateSSOAuthentication(new SSOAuthenticationPayload(
                userAuth,
                googleOAuthRequest.getClientId(),
                googleOAuthRequest.getClientSecret(),
                AuthProvider.google
        ));
        AuthResponse authResponse = authResponseGenerator.generateGoogleAuthResponse(authentication, httpServletRequest, GoogleRefreshTokenResponse.builder().accessToken(googleOAuthRequest.getAccessToken()).build());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        loginHistoryProcessor.process(authResponse, AuthProvider.email);
        return ResponseEntity.accepted().header(HttpHeaders.SET_COOKIE, authResponse.getRefreshTokenCookie().toString()).body(authResponse);
    }

    /**
     * Logs out the user by invalidating the refresh token and clearing the refresh token cookie.
     *
     * @param httpServletRequest  The HTTP request object.
     * @param httpServletResponse The HTTP response object.
     * @return A ResponseEntity containing an empty AuthResponse.
     */
    @Override
    public ResponseEntity<AuthResponse> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (Objects.isNull(cookies)) throw new AuthenticationException(ErrorData.NOT_LOGGED_IN);
        String cookieName = authenticationProperties.getJwt().getTokenData().getRefreshTokenCookieName();
        Optional<Cookie> refreshTokenCookie = CookieUtils.getCookie(httpServletRequest, cookieName);
        logoutUserProcessor.process(refreshTokenCookie);
        boolean isHttpOnly = true;
        if (refreshTokenCookie.isPresent()) {
            Cookie cookie = refreshTokenCookie.get();
            isHttpOnly = cookie.isHttpOnly();
        }
        assert cookieName != null;
        ResponseCookie cookie = ResponseCookie
                .from(cookieName, null)
                .httpOnly(isHttpOnly)
                .maxAge(1)
                .path("/")
                .sameSite("None")
                .secure(true)
                .build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(AuthResponse.builder().build());
    }
}