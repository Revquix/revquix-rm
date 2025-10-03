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
import com.revquix.sm.application.payload.ExceptionResponse;
import com.revquix.sm.application.payload.OutputStreamErrorPayload;
import com.revquix.sm.application.utils.OutputStreamUtil;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: JwtAuthenticationFilter.java
 */

/**
 * JwtAuthenticationFilter is a custom filter that intercepts incoming HTTP requests to
 * authenticate JWT tokens. It extracts the token from the Authorization header,
 * validates it, and sets the authentication in the SecurityContext if valid.
 * If the token is expired or malformed, it responds with an appropriate error message.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RevquixJwtTokenAuthenticator revquixJwtTokenAuthenticator;
    private final JwtDecoder jwtDecoder;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthenticationFilter >> doFilterInternal -> path: {}", request.getRequestURI());
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = extractTokenFromRequest(request);
        if (token != null) {
            try {
                jwtDecoder.decode(token);
            } catch (JwtValidationException exception) {
                Collection<OAuth2Error> errors = exception.getErrors();
                boolean isExpired = false;
                for (OAuth2Error error : errors) {
                    if (error.getDescription().contains("expired")) {
                        isExpired = true;
                        break;
                    }
                }
                ExceptionResponse exceptionResponse = ExceptionResponse
                        .builder()
                        .code(
                                isExpired ? ErrorData.TOKEN_EXPIRED.getCode() : ErrorData.MALFORMED_TOKEN.getCode())
                        .message(
                                isExpired ? ErrorData.TOKEN_EXPIRED.getMessage() : ErrorData.MALFORMED_TOKEN.getMessage()
                        )
                        .breadcrumbId(MDC.get(ServiceConstants.BREADCRUMB_ID))
                        .isTokenExpired(isExpired)
                        .localizedMessage(exception.getMessage())
                        .build();
                OutputStreamUtil.getOutputStream(new OutputStreamErrorPayload(HttpStatus.FORBIDDEN, exceptionResponse, response));
                log.error("{} >> ExceptionResponse >> JwtValidationException -> {}", getClass().getSimpleName(), exceptionResponse.toString());
                return;
            } catch (Exception exception) {
                ExceptionResponse exceptionResponse = ExceptionResponse
                        .builder()
                        .code(ErrorData.MALFORMED_TOKEN.getCode())
                        .message(ErrorData.MALFORMED_TOKEN.getMessage())
                        .breadcrumbId(MDC.get(ServiceConstants.BREADCRUMB_ID))
                        .isTokenExpired(false)
                        .localizedMessage(exception.getMessage())
                        .build();
                log.error("{} >> ExceptionResponse >> Jwt Exception -> {}", getClass().getSimpleName(), exceptionResponse.toString());
                OutputStreamUtil.getOutputStream(new OutputStreamErrorPayload(HttpStatus.UNAUTHORIZED, exceptionResponse, response));
                return;
            }
            AuthIdentity authIdentity = revquixJwtTokenAuthenticator.authenticateToken(token, response, request);
            if (authIdentity == null) return;
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        authIdentity,
                        null,
                        authIdentity.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        log.info("JwtAuthenticationFilter >> extractTokenFromRequest");
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        log.info("JwtAuthenticationFilter >> extractTokenFromRequest -> No bearer token present in the request");
        return null;
    }
}
