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
package com.revquix.sm.application.init;

/*
  Developer: Rohit Parihar
  Project: sana-health-backend
  GitHub: github.com/rohit-zip
  File: SanaApplicationRunner
 */

import com.revquix.sm.application.constants.ServiceConstants;
import com.revquix.sm.auth.processor.InitRoleProcessor;
import com.revquix.sm.auth.processor.InitScopeProcessor;
import com.revquix.sm.auth.processor.SuperClientGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * SanaApplicationRunner is an application runner that initializes roles, scopes,
 * and generates a super client upon application startup.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitializer implements ApplicationRunner {

    private final InitRoleProcessor initRoleProcessor;
    private final InitScopeProcessor initScopeProcessor;
    private final SuperClientGenerator superClientGenerator;

    /**
     * This method is executed after the application context is loaded.
     * It initializes roles, scopes, and generates a super client.
     *
     * @param args the application arguments
     * @throws Exception if any error occurs during processing
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Initiated Bloggios Application Runner");
        initRoleProcessor.process();
        initScopeProcessor.process();
        superClientGenerator.process();
        log.info(ServiceConstants.PROCESSING_TIME, "Bloggios Application Runner", System.currentTimeMillis() - startTime);
    }
}
