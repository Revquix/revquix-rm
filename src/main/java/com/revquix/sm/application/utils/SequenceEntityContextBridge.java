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
package com.revquix.sm.application.utils;

/*
  Developer: Rohit Parihar
  Project: ap-payment-service
  GitHub: github.com/rohit-zip
  File: SpringContextBridge
 */

import com.revquix.sm.application.dao.SequenceGeneratorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * SpringContextBridge is a utility class that provides static access to the
 * EntityPersistentService bean managed by Spring. This allows non-Spring
 * managed classes to access the service.
 */
@Component
public class SequenceEntityContextBridge {

    private static SequenceGeneratorDao sequenceService;

    /**
     * Autowired constructor to inject the EntityPersistentService dependency.
     * This allows static access to the service throughout the application.
     */
    @Autowired
    public SequenceEntityContextBridge(SequenceGeneratorDao injectedService) {
        SequenceEntityContextBridge.sequenceService = injectedService;
    }

    /**
     * Static method to retrieve the EntityPersistentService instance.
     * This can be used in non-Spring managed classes to access the service.
     *
     * @return the EntityPersistentService instance
     */
    public static SequenceGeneratorDao getSequenceService() {
        return sequenceService;
    }
}
