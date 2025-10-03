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
import com.revquix.sm.auth.repository.ScopeRepository;
import com.revquix.sm.auth.model.Scope;
import com.revquix.sm.auth.payload.ScopePayload;
import com.revquix.sm.auth.properties.FetchScopeProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: InitScopeProcessor.java
 */

/**
 * InitScopeProcessor is responsible for initializing and saving scopes
 * from configuration properties into the database.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InitScopeProcessor {

    private final FetchScopeProperties fetchScopeProperties;
    private final ScopeRepository scopeRepository;

    /**
     * Processes and saves scopes from configuration properties to the database.
     * If no scopes are found in the properties, the method exits early.
     * Logs the processing time for performance monitoring.
     */
    public void process() {
        long startTime = System.currentTimeMillis();
        log.info("Application Runner -> Scope Processor Initiated");
        Map<String, ScopePayload> scopes = fetchScopeProperties.data;
        if (scopes.isEmpty()) return;
        List<Scope> scopesToSave = scopes
                .values()
                .stream()
                .map(scopePayload -> Scope
                        .builder()
                        .scopeId(scopePayload.getId())
                        .scope(scopePayload.getScope())
                        .internalScopes(Objects.isNull(scopePayload.getInternalScopes()) ? null : scopePayload.getInternalScopes())
                        .description(scopePayload.getDescription())
                        .build()
                )
                .toList();
        log.info("Scope Processor -> Saving all the Scopes");
        scopeRepository.saveAllAndFlush(scopesToSave);
        log.info(ServiceConstants.PROCESSING_TIME, "Scope Processor", System.currentTimeMillis() - startTime);
    }
}
