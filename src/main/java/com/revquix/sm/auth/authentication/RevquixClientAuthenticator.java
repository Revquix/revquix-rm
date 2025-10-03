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

import com.revquix.sm.auth.repository.ClientRepository;
import com.revquix.sm.auth.enums.ClientStatus;
import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.application.exception.AuthenticationException;
import com.revquix.sm.auth.model.ClientAuth;
import com.revquix.sm.auth.payload.record.LoginCredentials;
import com.revquix.sm.auth.payload.record.LoginPrincipal;
import com.revquix.sm.auth.payload.record.SSOAuthenticationPayload;
import com.revquix.sm.application.utils.ValueCheckerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: BloggiosClientAuthenticator.java
 */

/**
 * SanaClientAuthenticator is responsible for authenticating clients based on various credentials.
 * It interacts with the ClientRepository to retrieve client information and validate credentials.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RevquixClientAuthenticator {

    private final ClientRepository clientRepository;

    public ClientAuth authenticateClient(LoginPrincipal loginPrincipal, LoginCredentials loginCredentials) {
        log.info("{} >> authenticateClient (loginPrincipal, loginCredentials)", getClass().getSimpleName());
        ValueCheckerUtil.isValidUUID(loginPrincipal.clientId(), ()-> new AuthenticationException(ErrorData.INVALID_CLIENT_ID));
        ClientAuth clientAuth = clientRepository.findById(loginPrincipal.clientId())
                .orElseThrow(() -> new AuthenticationException(ErrorData.CLIENT_NOT_FOUND_ID));
        Date expirationDate = clientAuth.getExpirationDate();
        if (!loginCredentials.clientSecret().equals(clientAuth.getClientSecret()))
            throw new AuthenticationException(ErrorData.INVALID_CLIENT_SECRET);
        if (new Date().after(expirationDate)) throw new AuthenticationException(ErrorData.CLIENT_CREDENTIALS_EXPIRED);
        validateClientStatus(clientAuth);
        return clientAuth;
    }

    public ClientAuth authenticateClient(String clientId) {
        log.info("{} >> authenticateClient (clientId)", getClass().getSimpleName());
        ClientAuth clientAuth = clientRepository.findById(clientId)
                .orElseThrow(() -> new AuthenticationException(ErrorData.CLIENT_NOT_FOUND_ID));
        Date expirationDate = clientAuth.getExpirationDate();
        if (new Date().after(expirationDate)) throw new AuthenticationException(ErrorData.CLIENT_CREDENTIALS_EXPIRED);
        validateClientStatus(clientAuth);
        return clientAuth;
    }

    public ClientAuth authenticateClient(SSOAuthenticationPayload ssoAuthenticationPayload) {
        log.info("{} >> authenticateClient (clientId, clientSecret)", getClass().getSimpleName());
        String clientId = ssoAuthenticationPayload.clientId();
        ValueCheckerUtil.isValidUUID(clientId, ()-> new AuthenticationException(ErrorData.INVALID_CLIENT_ID));
        ClientAuth clientAuth = clientRepository.findById(clientId)
                .orElseThrow(() -> new AuthenticationException(ErrorData.CLIENT_NOT_FOUND_ID));
        Date expirationDate = clientAuth.getExpirationDate();
        if (!ssoAuthenticationPayload.clientSecret().equals(clientAuth.getClientSecret()))
            throw new AuthenticationException(ErrorData.INVALID_CLIENT_SECRET);
        if (new Date().after(expirationDate)) throw new AuthenticationException(ErrorData.CLIENT_CREDENTIALS_EXPIRED);
        validateClientStatus(clientAuth);
        return clientAuth;
    }

    private void validateClientStatus(ClientAuth clientAuth) {
        ClientStatus clientStatus = clientAuth.getClientStatus();
        if (!clientStatus.equals(ClientStatus.ACTIVE))
            throw new AuthenticationException(ErrorData.CLIENT_STATUS_NOT_ACTIVE);
    }
}
