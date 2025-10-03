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
import com.revquix.sm.auth.repository.RoleRepository;
import com.revquix.sm.auth.model.Role;
import com.revquix.sm.auth.payload.RolePayload;
import com.revquix.sm.auth.properties.FetchRoleProperties;
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
 * File: InitRoleProcessor.java
 */

/**
 * InitRoleProcessor is responsible for initializing and saving roles
 * from configuration properties into the database.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InitRoleProcessor {

    private final FetchRoleProperties fetchRoleProperties;
    private final RoleRepository roleRepository;

    /**
     * Processes and saves roles from the configuration properties to the database.
     * If no roles are found in the properties, the method exits early.
     * Logs the processing time for performance monitoring.
     */
    public void process() {
        long startTime = System.currentTimeMillis();
        log.info("Application Runner -> Role Processor Initiated");
        Map<String, RolePayload> roles = fetchRoleProperties.data;
        if (roles.isEmpty()) return;
        List<Role> rolesToSave = roles
                .values()
                .stream()
                .map(rolePayload -> Role
                        .builder()
                        .roleId(rolePayload.getId())
                        .role(rolePayload.getRole())
                        .internalRoles(Objects.isNull(rolePayload.getInternalRoles()) ? null : rolePayload.getInternalRoles())
                        .description(rolePayload.getDescription())
                        .build()
                )
                .toList();
        log.info("Role Processor -> Saving all the Roles");
        roleRepository.saveAll(rolesToSave);
        log.info(ServiceConstants.PROCESSING_TIME, "Role Processor", System.currentTimeMillis() - startTime);
    }
}
