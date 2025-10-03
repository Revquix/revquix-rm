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

import com.revquix.sm.application.constants.EnvironmentConstants;
import com.revquix.sm.application.constants.ServiceConstants;
import com.revquix.sm.auth.enums.AuthenticationType;
import com.revquix.sm.auth.payload.record.RefreshTokenRecord;
import com.revquix.sm.auth.properties.AuthenticationProperties;
import com.revquix.sm.application.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: JwtTokenGenerator.java
 */

/**
 * JwtTokenGenerator is responsible for generating JWT access tokens and refresh tokens
 * for authenticated users and clients. It uses JwtEncoder to create the tokens with
 * appropriate claims based on the authentication type (user login or client login).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenGenerator {

    private final JwtEncoder jwtEncoder;
    private final AuthenticationProperties authenticationProperties;
    private final Environment environment;
    private final IpUtils ipUtils;

    public String generateAccessToken(Authentication authentication, HttpServletRequest httpServletRequest) {
        AuthIdentity authIdentity = (AuthIdentity) authentication.getPrincipal();
        log.info("Generating Access Token for User/Client -> clientId: {}", authIdentity.getClientId());
        AuthenticationType authenticationType = authIdentity.getAuthenticationType();
        return switch (authenticationType) {
            case USER_LOGIN -> generateUserAccessToken(authIdentity, httpServletRequest);
            case CLIENT_LOGIN -> generateClientAccessToken(authIdentity, httpServletRequest);
        };
    }

    public RefreshTokenRecord generateRefreshToken(Authentication authentication, HttpServletRequest httpServletRequest) {
        AuthIdentity authIdentity = (AuthIdentity) authentication.getPrincipal();
        log.info("Generating Refresh Token for User/Client -> clientId: {}", authIdentity.getClientId());
        AuthenticationType authenticationType = authIdentity.getAuthenticationType();
        return switch (authenticationType) {
            case USER_LOGIN -> generateUserRefreshToken(authIdentity, httpServletRequest);
            case CLIENT_LOGIN -> generateClientRefreshToken(authIdentity, httpServletRequest);
        };
    }

    private RefreshTokenRecord generateClientRefreshToken(AuthIdentity authIdentity, HttpServletRequest httpServletRequest) {
        Instant now = Instant.now();
        AuthenticationProperties.TokenData tokenData = authenticationProperties.getJwt().getTokenData();
        UUID jti = UUID.randomUUID();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet
                .builder()
                .issuedAt(now)
                .expiresAt(now.plus(tokenData.getRefreshTokenExpiryDays(), ChronoUnit.DAYS))
                .subject(authIdentity.getClientId())
                .issuer(ServiceConstants.REVQUIX)
                .claim(ServiceConstants.JTI, jti.toString())
                .claim(ServiceConstants.CLIENT_ID, authIdentity.getClientId())
                .claim(ServiceConstants.TOKEN_TYPE, ServiceConstants.REFRESH_TOKEN_TYPE)
                .claim("environment", Objects.requireNonNull(environment.getProperty(EnvironmentConstants.ACTIVE_PROFILE)))
                .claim(ServiceConstants.REMOTE_ADDRESS, ipUtils.getRemoteAddress())
                .claim(ServiceConstants.ORIGINS, CollectionUtils.isEmpty(authIdentity.getOrigins()) ? List.of(ServiceConstants.UNIVERSAL_ORIGIN) : authIdentity.getOrigins())
                .claim(ServiceConstants.CLIENT_NAME, authIdentity.getClientName())
                .claim(ServiceConstants.CLIENT_TYPE, authIdentity.getClientType())
                .claim(ServiceConstants.AUTHENTICATION_TYPE, authIdentity.getAuthenticationType().name())
                .build();
        return new RefreshTokenRecord(jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue(), jti.toString());
    }

    private RefreshTokenRecord generateUserRefreshToken(AuthIdentity authIdentity, HttpServletRequest httpServletRequest) {
        Instant now = Instant.now();
        AuthenticationProperties.TokenData tokenData = authenticationProperties.getJwt().getTokenData();
        UUID jti = UUID.randomUUID();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet
                .builder()
                .issuedAt(now)
                .expiresAt(now.plus(tokenData.getRefreshTokenExpiryDays(), ChronoUnit.DAYS))
                .subject(authIdentity.getClientId())
                .issuer(ServiceConstants.REVQUIX)
                .claim(ServiceConstants.JTI, jti.toString())
                .claim(ServiceConstants.USER_ID, authIdentity.getUserId())
                .claim(ServiceConstants.CLIENT_ID, authIdentity.getClientId())
                .claim(ServiceConstants.TOKEN_TYPE, ServiceConstants.REFRESH_TOKEN_TYPE)
                .claim("environment", Objects.requireNonNull(environment.getProperty(EnvironmentConstants.ACTIVE_PROFILE)))
                .claim(ServiceConstants.REMOTE_ADDRESS, ipUtils.getRemoteAddress())
                .claim(ServiceConstants.ORIGINS, CollectionUtils.isEmpty(authIdentity.getOrigins()) ? "0.0.0.0" : authIdentity.getOrigins())
                .claim(ServiceConstants.CLIENT_NAME, authIdentity.getClientName())
                .claim(ServiceConstants.AUTHENTICATION_TYPE, authIdentity.getAuthenticationType().name())
                .claim(ServiceConstants.CLIENT_TYPE, authIdentity.getClientType())
                .build();
        return new RefreshTokenRecord(jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue(), jti.toString());
    }

    private String generateClientAccessToken(AuthIdentity authIdentity, HttpServletRequest httpServletRequest) {
        List<String> authorities = authIdentity.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        String origin = httpServletRequest.getHeader(ServiceConstants.ORIGIN);
        Instant now = Instant.now();
        AuthenticationProperties.TokenData tokenData = authenticationProperties.getJwt().getTokenData();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet
                .builder()
                .issuedAt(now)
                .expiresAt(now.plus(Objects.isNull(origin) ? tokenData.getLongAccessTokenExpiryMinutes() : tokenData.getAccessTokenExpiryMinutes(), ChronoUnit.MINUTES))
                .subject(authIdentity.getClientId())
                .issuer(ServiceConstants.REVQUIX)
                .claim(ServiceConstants.AUTHORITIES, authorities)
                .claim(ServiceConstants.SCOPES, authIdentity.getScopes())
                .claim(ServiceConstants.CLIENT_ID, authIdentity.getClientId())
                .claim(ServiceConstants.TOKEN_TYPE, ServiceConstants.ACCESS_TOKEN_TYPE)
                .claim("environment", Objects.requireNonNull(environment.getProperty(EnvironmentConstants.ACTIVE_PROFILE)))
                .claim(ServiceConstants.REMOTE_ADDRESS, ipUtils.getRemoteAddress())
                .claim(ServiceConstants.ORIGINS, CollectionUtils.isEmpty(authIdentity.getOrigins()) ? "0.0.0.0" : authIdentity.getOrigins())
                .claim(ServiceConstants.CLIENT_NAME, authIdentity.getClientName())
                .claim(ServiceConstants.AUTHENTICATION_TYPE, authIdentity.getAuthenticationType().name())
                .claim(ServiceConstants.CLIENT_TYPE, authIdentity.getClientType().name())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    private String generateUserAccessToken(AuthIdentity authIdentity, HttpServletRequest httpServletRequest) {
        List<String> authorities = authIdentity.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        String origin = httpServletRequest.getHeader(ServiceConstants.ORIGIN);
        Instant now = Instant.now();
        AuthenticationProperties.TokenData tokenData = authenticationProperties.getJwt().getTokenData();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet
                .builder()
                .issuedAt(now)
                .expiresAt(now.plus(Objects.isNull(origin) ? tokenData.getLongAccessTokenExpiryMinutes() : tokenData.getAccessTokenExpiryMinutes(), ChronoUnit.MINUTES))
                .subject(authIdentity.getClientId())
                .issuer(ServiceConstants.REVQUIX)
                .claim(ServiceConstants.AUTHORITIES, authorities)
                .claim(ServiceConstants.USER_ID, authIdentity.getUserId())
                .claim(ServiceConstants.USERNAME, authIdentity.getUsername())
                .claim(ServiceConstants.EMAIL, authIdentity.getEmail())
                .claim(ServiceConstants.ROLES, authIdentity.getRoles())
                .claim(ServiceConstants.SCOPES, authIdentity.getScopes())
                .claim(ServiceConstants.CLIENT_ID, authIdentity.getClientId())
                .claim(ServiceConstants.TOKEN_TYPE, ServiceConstants.ACCESS_TOKEN_TYPE)
                .claim("environment", Objects.requireNonNull(environment.getProperty(EnvironmentConstants.ACTIVE_PROFILE)))
                .claim(ServiceConstants.REMOTE_ADDRESS, ipUtils.getRemoteAddress())
                .claim(ServiceConstants.ORIGINS, CollectionUtils.isEmpty(authIdentity.getOrigins()) ? "0.0.0.0" : authIdentity.getOrigins())
                .claim(ServiceConstants.CLIENT_NAME, authIdentity.getClientName())
                .claim(ServiceConstants.AUTHENTICATION_TYPE, authIdentity.getAuthenticationType().name())
                .claim(ServiceConstants.CLIENT_TYPE, authIdentity.getClientType())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }
}
