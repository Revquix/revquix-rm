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

import com.revquix.sm.application.constants.ServiceConstants;
import com.revquix.sm.auth.repository.ClientRepository;
import com.revquix.sm.auth.repository.ScopeRepository;
import com.revquix.sm.auth.enums.ClientStatus;
import com.revquix.sm.auth.enums.ClientType;
import com.revquix.sm.auth.enums.EnvironmentType;
import com.revquix.sm.auth.model.ClientAuth;
import com.revquix.sm.auth.model.Scope;
import com.revquix.sm.application.utils.DateUtils;
import com.revquix.sm.application.utils.RandomGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: SuperClientGenerator.java
 */

/**
 * SuperClientGenerator is responsible for generating or updating a default super client
 * with all available scopes in the system. If the client already exists, it updates its scopes.
 * If not, it creates a new client with a generated secret and logs the credentials.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SuperClientGenerator {

    private final ScopeRepository scopeRepository;
    private final ClientRepository clientRepository;

    /**
     * Processes the generation or update of the super client.
     * If the client with the predefined UUID exists, it updates its scopes.
     * If not, it creates a new client with all scopes and logs the credentials.
     */
    public void process() {
        long startTime = System.currentTimeMillis();
        log.info("Application Runner -> Super Client Generator");
        Optional<ClientAuth> clientOptional = clientRepository.findById("2dfca67b-2c82-41d2-bbda-bb426621dfb9");
        List<Scope> scopes = scopeRepository.findAll();
        if (clientOptional.isPresent()) {
            ClientAuth clientAuth = clientOptional.get();
            clientAuth.setScopes(scopes);
            clientRepository.save(clientAuth);
            log.info("Default Client Details Updated. Please do not use the default client credentials for production");
        } else {
            ClientAuth clientAuth = ClientAuth
                    .builder()
                    .clientId("2dfca67b-2c82-41d2-bbda-bb426621dfb9")
                    .clientName("System Generated Default Client")
                    .clientType(ClientType.API_TESTING)
                    .environmentType(EnvironmentType.DEVELOPMENT)
                    .clientSecret(RandomGenerator.generateClientSecret())
                    .clientStatus(ClientStatus.ACTIVE)
                    .dateGenerated(new Date())
                    .expirationDate(DateUtils.addDays(new Date(), 700))
                    .scopes(scopes)
                    .build();
            ClientAuth clientAuthResponse = clientRepository.save(clientAuth);
            log.info("""
                Default Client Secret has been generated
                Please find the details below
                
                ********************************************
                
                clientId: {}
                clientSecret: {}
                
                ********************************************
                
                Note: Below client details will be expired in 24 hours.
                """, clientAuthResponse.getClientId(), clientAuthResponse.getClientSecret());
        }
        log.info(ServiceConstants.PROCESSING_TIME, "Super Client Generator", System.currentTimeMillis() - startTime);
    }
}
