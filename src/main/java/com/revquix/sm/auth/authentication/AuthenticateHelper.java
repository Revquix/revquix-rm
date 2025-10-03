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

import com.revquix.sm.auth.enums.AuthenticationType;
import com.revquix.sm.auth.model.ClientAuth;
import com.revquix.sm.auth.model.RefreshToken;
import com.revquix.sm.auth.model.UserAuth;
import com.revquix.sm.auth.payload.record.LoginCredentials;
import com.revquix.sm.auth.payload.record.LoginPrincipal;
import com.revquix.sm.auth.payload.record.SSOAuthenticationPayload;
import com.revquix.sm.auth.utils.RolesParserUtil;
import com.revquix.sm.auth.utils.ScopeParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: AuthenticateHelper.java
 */

/**
 * AuthenticateHelper is a utility class that provides methods to generate authentication tokens
 * based on different authentication mechanisms such as user login, client login, refresh tokens,
 * and SSO authentication. It leverages SanaUserAuthenticator and SanaClientAuthenticator to
 * validate credentials and retrieve user and client information.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticateHelper {

    private final RevquixUserAuthenticator revquixUserAuthenticator;
    private final RevquixClientAuthenticator revquixClientAuthenticator;

    public AuthenticationType getAuthenticationType(LoginPrincipal loginPrincipal, LoginCredentials loginCredentials) {
        log.info("{} >> getAuthenticationType", getClass().getSimpleName());
        if (
                (Objects.isNull(loginPrincipal.entrypoint()) && Objects.isNull(loginCredentials.password()))
                        && (Objects.nonNull(loginPrincipal.clientId()) && Objects.nonNull(loginCredentials.clientSecret()))
        )
            return AuthenticationType.CLIENT_LOGIN;
        return AuthenticationType.USER_LOGIN;
    }

    public UsernamePasswordAuthenticationToken generateAuthentication(LoginPrincipal principal, LoginCredentials credentials) {
        log.info("{} >> generateAuthentication (principal, credentials)", getClass().getSimpleName());
        UserAuth userAuth = null;
        List<String> roles = Collections.emptyList();
        AuthenticationType authenticationType = getAuthenticationType(principal, credentials);
        if (authenticationType.equals(AuthenticationType.USER_LOGIN)) {
            log.info("{} >> generateAuthentication (principal, credentials) -> Bloggios User login detected with entrypoint: {}", getClass().getSimpleName(), principal.entrypoint());
            userAuth = revquixUserAuthenticator.authenticateUser(principal, credentials);
            roles = Collections.unmodifiableList(RolesParserUtil.parseRoles(userAuth.getRoles()));
        }
        ClientAuth clientAuth = revquixClientAuthenticator.authenticateClient(principal, credentials);
        return getUsernamePasswordAuthenticationToken(authenticationType, clientAuth, roles, userAuth);
    }

    public UsernamePasswordAuthenticationToken generateAuthentication(RefreshToken refreshToken) {
        log.info("{} >> generateAuthentication (refreshToken)", getClass().getSimpleName());
        UserAuth userAuth = null;
        List<String> roles = Collections.emptyList();
        AuthenticationType authenticationType = refreshToken.getAuthenticationType();
        if (authenticationType.equals(AuthenticationType.USER_LOGIN)) {
            userAuth = revquixUserAuthenticator.authenticateRefreshTokenUser(refreshToken.getUserId());
            roles = Collections.unmodifiableList(RolesParserUtil.parseRoles(userAuth.getRoles()));
        }
        ClientAuth clientAuth = revquixClientAuthenticator.authenticateClient(refreshToken.getClientId());
        return getUsernamePasswordAuthenticationToken(authenticationType, clientAuth, roles, userAuth);
    }

    public UsernamePasswordAuthenticationToken generateSSOAuthentication(SSOAuthenticationPayload ssoAuthenticationPayload) {
        log.info("{} >> generateAuthentication (ssoAuthenticationPayload)", getClass().getSimpleName());
        revquixUserAuthenticator.authenticateSSOUser(ssoAuthenticationPayload);
        List<String> roles = Collections.unmodifiableList(RolesParserUtil.parseRoles(ssoAuthenticationPayload.userAuth().getRoles()));
        ClientAuth clientAuth = revquixClientAuthenticator.authenticateClient(ssoAuthenticationPayload);
        return getUsernamePasswordAuthenticationToken(AuthenticationType.USER_LOGIN, clientAuth, roles, ssoAuthenticationPayload.userAuth());
    }

    private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(AuthenticationType authenticationType, ClientAuth clientAuth, List<String> roles, UserAuth userAuth) {
        log.info("{} >> generateUsernamePasswordAuthenticationToken (authenticationType, clientAuth, roles, userAuth)", getClass().getSimpleName());
        List<String> scopes = Collections.unmodifiableList(ScopeParserUtil.parseScopes(clientAuth.getScopes()));
        List<String> authoritiesString = Stream
                .concat(roles.stream(), scopes.stream())
                .toList();
        List<SimpleGrantedAuthority> authorities = authoritiesString
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        AuthIdentity authIdentity = getAuthIdentity(userAuth, clientAuth, authorities, authenticationType, roles, scopes);
        return new UsernamePasswordAuthenticationToken(authIdentity, null, authorities);
    }

    private AuthIdentity getAuthIdentity(
            UserAuth userAuth,
            ClientAuth clientAuth,
            Collection<? extends GrantedAuthority> authorities,
            AuthenticationType authenticationType,
            List<String> roles,
            List<String> scopes
    ) {
        log.info("{} >> getAuthIdentity -> Generating AuthIdentity", getClass().getSimpleName());
        AuthIdentity authIdentity = AuthIdentity
                .builder()
                .clientId(clientAuth.getClientId().toString())
                .clientName(clientAuth.getClientName())
                .roles(roles)
                .scopes(scopes)
                .origins(clientAuth.getOrigins())
                .clientType(clientAuth.getClientType())
                .authorities(authorities)
                .authenticationType(authenticationType)
                .build();
        if (authenticationType.equals(AuthenticationType.USER_LOGIN) && Objects.nonNull(userAuth)) {
            authIdentity.setUserId(userAuth.getUserId().toString());
            authIdentity.setUsername(userAuth.getUsername());
            authIdentity.setEmail(userAuth.getEmail());
            authIdentity.setMobile(userAuth.getMobile());
            authIdentity.setProviders(userAuth.getAuthProvider());
            authIdentity.setHasPassword(Objects.nonNull(userAuth.getPassword()));
            authIdentity.setLastPasswordChange(userAuth.getLastPasswordChange());
        }
        return authIdentity;
    }
}
