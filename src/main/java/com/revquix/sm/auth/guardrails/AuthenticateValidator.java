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

import com.revquix.sm.application.guardrails.EmailValidator;
import com.revquix.sm.auth.authentication.AuthIdentity;
import com.revquix.sm.application.constants.ServiceConstants;
import com.revquix.sm.auth.enums.AuthenticationType;
import com.revquix.sm.auth.enums.EntrypointType;
import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.application.exception.AuthenticationException;
import com.revquix.sm.auth.payload.request.AuthenticateRequest;
import com.revquix.sm.auth.properties.AuthorizationProperties;
import com.revquix.sm.auth.utils.EntrypointTypeUtil;
import com.revquix.sm.application.utils.ValueCheckerUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: AuthenticateValidator.java
 */

/**
 * AuthenticateValidator is responsible for validating authentication requests
 * and ensuring that the request origins are authorized based on client credentials.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticateValidator {

    private final UsernameValidator usernameValidator;
    private final EmailValidator emailValidator;
    private final MobileNumberValidator mobileNumberValidator;
    private final AuthorizationProperties authorizationProperties;

    /**
     * Validates the given AuthenticateRequest.
     *
     * @param request The authentication request to validate.
     * @throws AuthenticationException if any validation fails.
     */
    public void validateAuthenticateRequest(AuthenticateRequest request) {
        log.info("{} >> validateAuthenticateRequest -> clientId: {}, entrypoint: {}", getClass().getSimpleName(), request.getClientId(), request.getEntrypoint());
        if (Objects.isNull(request.getClientId()))
            throw new AuthenticationException(ErrorData.CLIENT_ID_MANDATORY);
        if (Objects.isNull(request.getClientSecret()))
            throw new AuthenticationException(ErrorData.CLIENT_SECRET_MANDATORY);
        ValueCheckerUtil.isValidUUID(request.getClientId(), () -> new AuthenticationException(ErrorData.INVALID_CLIENT_ID));
        if (Objects.nonNull(request.getEntrypoint()) && Objects.isNull(request.getPassword()))
            throw new AuthenticationException(ErrorData.PASSWORD_MANDATORY);
        if (Objects.nonNull(request.getPassword()) && Objects.isNull(request.getEntrypoint()))
            throw new AuthenticationException(ErrorData.ENTRYPOINT_MANDATORY);
        validateEntryPoint(request);
    }

    public void validateOrigins(Authentication authentication, HttpServletRequest httpServletRequest) {
        log.info("{} >> validateOrigins -> Validating request origin with client credentials", getClass().getSimpleName());
        String origin = httpServletRequest.getHeader(ServiceConstants.ORIGIN);
        AuthIdentity authIdentity = (AuthIdentity) authentication.getPrincipal();
        if (ObjectUtils.isEmpty(origin))
            validateNullOrigin(authIdentity);
        else
            validateForEnvironment(authIdentity, origin);
    }

    private void validateForEnvironment(AuthIdentity authIdentity, String origin) {
        log.info("Authenticate Request for {} -> clientId: {}", authIdentity.getClientName(), authIdentity.getClientId());
        List<String> origins = authIdentity.getOrigins();
        if (CollectionUtils.isEmpty(origins)) return;
        boolean isPresent = origins.contains(origin);
        if (!isPresent) {
            log.info("Request with Origin {} is not allowed for given Client -> clientId: {}", origin, authIdentity.getClientId());
            throw new AuthenticationException(ErrorData.AUTHORITIES_MISSING);
        }
    }

    private void validateNullOrigin(AuthIdentity authIdentity) {
        log.info("Authenticate Request for Development -> clientId: {}", authIdentity.getClientId());
        boolean isAuthorized = checkAllAuthoritiesContain(authIdentity);
        if (!isAuthorized)
            throw new AuthenticationException(ErrorData.AUTHORITIES_MISSING);
    }

    private boolean checkAllAuthoritiesContain(AuthIdentity authIdentity) {
        Map<String, List<String>> rolesToAllow = authorizationProperties.getJwt().getRolesToAllow();
        List<String> requiredAuthorities = rolesToAllow.get(authIdentity.getAuthenticationType().equals(AuthenticationType.USER_LOGIN) ? "development" : "clientDevelopment");
        List<String> authenticatedAuthorities = authIdentity.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        Map<String, Integer> frequencyMap = new HashMap<>();
        authenticatedAuthorities.forEach(authority -> {
            frequencyMap.put(authority, frequencyMap.getOrDefault(authority, 0) + 1);
        });
        for (String requiredAuthority : requiredAuthorities) {
            if (frequencyMap.containsKey(requiredAuthority) && frequencyMap.get(requiredAuthority) > 0)
                frequencyMap.put(requiredAuthority, frequencyMap.get(requiredAuthority) - 1);
            else
                return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private void validateEntryPoint(AuthenticateRequest request) {
        log.info("{} >> validateEntryPoint -> entrypoint: {}", getClass().getSimpleName(), request.getEntrypoint());
        if (Objects.nonNull(request.getEntrypoint())) {
            EntrypointType entrypointType = EntrypointTypeUtil.parseEntrypoint(request.getEntrypoint());
            if (entrypointType.equals(EntrypointType.username))
                usernameValidator.isValidUsername(request.getEntrypoint(), () -> new AuthenticationException(ErrorData.INVALID_USERNAME));
            else if (entrypointType.equals(EntrypointType.email))
                emailValidator.isValidEmail(request.getEntrypoint(), () -> new AuthenticationException(ErrorData.INVALID_EMAIL));
            else
                mobileNumberValidator.isValidMobileNumber(request.getEntrypoint(), () -> new AuthenticationException(ErrorData.INVALID_MOBILE_NUMBER));
        }
    }
}
