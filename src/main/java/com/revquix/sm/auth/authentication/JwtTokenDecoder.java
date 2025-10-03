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
import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.application.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: JwtTokenDecoder.java
 */

/**
 * JwtTokenDecoder is responsible for decoding and validating JWT tokens.
 * It extracts various claims from the JWT, such as user ID, username, email,
 * roles, scopes, and authorities. It also handles token validation and throws
 * appropriate exceptions for expired or malformed tokens.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenDecoder {

    private final JwtDecoder jwtDecoder;

    public Collection<? extends GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<String> authorities = jwt.getClaimAsStringList(ServiceConstants.AUTHORITIES);
        return authorities
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @SneakyThrows
    public Jwt validateNative(String jwtToken) {
        log.info("{} >> validateNative -> Validating the token with throwable exceptions", getClass().getSimpleName());
        try {
            return jwtDecoder.decode(jwtToken);
        } catch (JwtValidationException exception) {
            Collection<OAuth2Error> errors = exception.getErrors();
            boolean isExpired = false;
            for (OAuth2Error error : errors) {
                if (error.getDescription().contains("expired")) {
                    isExpired = true;
                    break;
                }
            }
            if (isExpired) throw new AuthenticationException(ErrorData.REFRESH_TOKEN_EXPIRED);
            else throw new AuthenticationException(ErrorData.MALFORMED_TOKEN);
        } catch (BadJwtException exception) {
            throw new AuthenticationException(ErrorData.MALFORMED_TOKEN);
        }
    }

    public String extractUserId(Jwt jwt) {
        return jwt.getClaimAsString(ServiceConstants.USER_ID);
    }

    public String extractUsername(Jwt jwt) {
        return jwt.getClaimAsString(ServiceConstants.USERNAME);
    }

    public String extractEmail(Jwt jwt) {
        return jwt.getClaimAsString(ServiceConstants.EMAIL);
    }

    public String extractClientId(Jwt jwt) {
        return jwt.getSubject();
    }

    public List<String> extractRoles(Jwt jwt) {
        return jwt.getClaimAsStringList(ServiceConstants.ROLES);
    }

    public List<String> extractScopes(Jwt jwt) {
        return jwt.getClaimAsStringList(ServiceConstants.SCOPES);
    }

    public String extractTokenType(Jwt jwt) {
        return jwt.getClaimAsString(ServiceConstants.TOKEN_TYPE);
    }

    public List<String> extractOrigins(Jwt jwt) {
        return jwt.getClaimAsStringList(ServiceConstants.ORIGINS);
    }

    public String extractRemoteAddress(Jwt jwt) {
        return jwt.getClaimAsString(ServiceConstants.REMOTE_ADDRESS);
    }

    public String extractJti(Jwt jwt) {
        return jwt.getClaimAsString(ServiceConstants.JTI);
    }

    public String extractAuthenticationType(Jwt jwt) {
        return jwt.getClaimAsString(ServiceConstants.AUTHENTICATION_TYPE);
    }

    public String extractClientName(Jwt jwt) {
        return jwt.getClaimAsString(ServiceConstants.CLIENT_NAME);
    }

    public String extractClientType(Jwt jwt) {
        return jwt.getClaimAsString(ServiceConstants.CLIENT_TYPE);
    }
}
