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

import com.revquix.sm.application.constants.ServiceConstants;
import com.revquix.sm.auth.enums.AuthenticationType;
import com.revquix.sm.auth.enums.ClientType;
import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.application.payload.ExceptionResponse;
import com.revquix.sm.application.payload.OutputStreamErrorPayload;
import com.revquix.sm.auth.properties.AuthenticationProperties;
import com.revquix.sm.application.utils.IpUtils;
import com.revquix.sm.application.utils.OutputStreamExceptionGenerator;
import com.revquix.sm.application.utils.OutputStreamUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: BloggiosJwtTokenAuthenticator.java
 */

/**
 * SanaJwtTokenAuthenticator is responsible for authenticating JWT tokens.
 * It verifies the token type, authentication type, and performs additional
 * checks such as remote address validation and path authentication based
 * on the token's claims and application properties.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RevquixJwtTokenAuthenticator {

    private final JwtTokenDecoder jwtTokenDecoder;
    private final JwtDecoder jwtDecoder;
    private final AuthenticationProperties authenticationProperties;
    private final AntPathMatcher antPathMatcher;
    private final Environment environment;
    private final IpUtils ipUtils;

    public AuthIdentity authenticateToken(String token, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
        log.info("{} >> authenticationToken", getClass().getSimpleName());
        Jwt jwt = jwtDecoder.decode(token);
        String authenticationType = jwtTokenDecoder.extractAuthenticationType(jwt);
        boolean isDone = authenticateTokenType(httpServletResponse, jwt);
        if (!isDone) return null;
        AuthenticationType type = AuthenticationType.valueOf(authenticationType);
        AuthIdentity authIdentity;
        if (type.equals(AuthenticationType.USER_LOGIN))
            authIdentity = authenticateUserLoginType(jwt, httpServletResponse, httpServletRequest);
        else
            authIdentity = authenticateClientLoginType(jwt, httpServletResponse, httpServletRequest);
        return authIdentity;
    }

    private AuthIdentity authenticateClientLoginType(Jwt jwt, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
        log.info("{} >> authenticateClientLoginType", getClass().getSimpleName());
        boolean isPathAuthenticationDone = authenticatePaths(httpServletResponse, httpServletRequest);
        if (!isPathAuthenticationDone) return null;
        AuthIdentity authIdentity = generateAuthIdentityClient(jwt);
        boolean isDone = authenticateRemoteAddress(authIdentity, httpServletRequest, httpServletResponse);
        if (!isDone) return null;
        return authIdentity;
    }

    private AuthIdentity generateAuthIdentityClient(Jwt jwt) {
        log.info("{} >> generateAuthIdentityClient", getClass().getSimpleName());
        return AuthIdentity
                .builder()
                .clientId(jwtTokenDecoder.extractClientId(jwt))
                .clientName(jwtTokenDecoder.extractClientName(jwt))
                .scopes(jwtTokenDecoder.extractScopes(jwt))
                .origins(jwtTokenDecoder.extractOrigins(jwt))
                .clientType(ClientType.valueOf(jwtTokenDecoder.extractClientType(jwt)))
                .authorities(jwtTokenDecoder.extractAuthorities(jwt))
                .authenticationType(AuthenticationType.valueOf(jwtTokenDecoder.extractAuthenticationType(jwt)))
                .remoteAddress(jwtTokenDecoder.extractRemoteAddress(jwt))
                .build();
    }

    private boolean authenticateRemoteAddress(AuthIdentity authIdentity, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.info("{} >> authenticateRemoteAddress", getClass().getSimpleName());
        String remoteAddress = authIdentity.getRemoteAddress();
        String address = ipUtils.getRemoteAddress();
        if (!remoteAddress.equals(address)) {
            log.error("Error -> {} >> Remote Address Authentication Failed", getClass().getSimpleName());
            OutputStreamExceptionGenerator.generateExceptionResponse(ErrorData.REMOTE_ADDRESS_AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED, httpServletResponse);
            return false;
        }
        return true;
    }

    private boolean authenticatePaths(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
        log.info("{} >> Authenticating Paths", getClass().getSimpleName());
        List<String> clientPaths = authenticationProperties.getExcludedPaths().getJwtPaths().getClientAuthenticationPaths();
        List<String> excludePaths = authenticationProperties.getExcludedPaths().getJwtPaths().getExcludePaths();
        List<String> allPaths = Stream.concat(clientPaths.stream(), excludePaths.stream()).collect(Collectors.toSet()).stream().toList();
        String requestURI = httpServletRequest.getRequestURI();
        boolean isPathMatch = false;
        for (String path : allPaths) {
            String contextPath = environment.getProperty("server.servlet.context-path") != null ? environment.getProperty("server.servlet.context-path") : "";
            boolean match = antPathMatcher.match(contextPath + path, requestURI);
            if (match) {
                isPathMatch = true;
                break;
            }
        }
        if (!isPathMatch) {
            log.error("Error -> {} >> Full User Authentication is required to access this resource -> path: {}", getClass().getSimpleName(), requestURI);
            OutputStreamExceptionGenerator.generateExceptionResponse(ErrorData.FULL_AUTHENTICATION_IS_REQUIRED, HttpStatus.UNAUTHORIZED, httpServletResponse);
            return false;
        }
        return true;
    }

    private AuthIdentity authenticateUserLoginType(Jwt jwt, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
        log.info("{} >> authenticateUserLoginType", getClass().getSimpleName());
        AuthIdentity authIdentity = generateAuthIdentityUser(jwt);
        boolean isDone = true;
        if (authenticationProperties.getJwt().getTokenData().getIsRemoteAddressAuthentication()) {
            isDone = authenticateRemoteAddress(authIdentity, httpServletRequest, httpServletResponse);
        }
        if (!isDone) return null;
        return authIdentity;
    }

    private AuthIdentity generateAuthIdentityUser(Jwt jwt) {
        log.info("{} >> generateAuthIdentityUser", getClass().getSimpleName());
        return AuthIdentity
                .builder()
                .userId(jwtTokenDecoder.extractUserId(jwt))
                .clientId(jwtTokenDecoder.extractClientId(jwt))
                .username(jwtTokenDecoder.extractUsername(jwt))
                .email(jwtTokenDecoder.extractEmail(jwt))
                .clientName(jwtTokenDecoder.extractClientName(jwt))
                .roles(jwtTokenDecoder.extractRoles(jwt))
                .scopes(jwtTokenDecoder.extractScopes(jwt))
                .origins(jwtTokenDecoder.extractOrigins(jwt))
                .clientType(ClientType.valueOf(jwtTokenDecoder.extractClientType(jwt)))
                .authorities(jwtTokenDecoder.extractAuthorities(jwt))
                .authenticationType(AuthenticationType.valueOf(jwtTokenDecoder.extractAuthenticationType(jwt)))
                .remoteAddress(jwtTokenDecoder.extractRemoteAddress(jwt))
                .build();
    }

    @SneakyThrows(value = {IOException.class})
    private boolean authenticateTokenType(HttpServletResponse httpServletResponse, Jwt jwt) {
        log.info("{} >> authenticateTokenType", getClass().getSimpleName());
        String tokenType = jwtTokenDecoder.extractTokenType(jwt);
        if (tokenType.equals(ServiceConstants.REFRESH_TOKEN_TYPE)) {
            ExceptionResponse exceptionResponse = ExceptionResponse
                    .builder()
                    .code(ErrorData.REFRESH_TOKEN_NOT_ALLOWED.getCode())
                    .message(ErrorData.REFRESH_TOKEN_NOT_ALLOWED.getMessage())
                    .breadcrumbId(MDC.get(ServiceConstants.BREADCRUMB_ID))
                    .isTokenExpired(false)
                    .build();
            log.error("Error -> {} >> authenticateTokenType -> ErrorData: {}", getClass().getSimpleName(), exceptionResponse);
            OutputStreamUtil.getOutputStream(new OutputStreamErrorPayload(HttpStatus.UNAUTHORIZED, exceptionResponse, httpServletResponse));
            return false;
        }
        return true;
    }
}
