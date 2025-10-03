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
  Project: ap-auth-provider
  GitHub: github.com/rohit-zip
  File: ControllerHelper
 */

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

/**
 * ControllerHelper provides utility methods for controller operations,
 * including logging and measuring processing time for API calls.
 */
@UtilityClass
public class ControllerHelper {

    /**
     * Logs the initiation and processing time of an API call.
     *
     * @param responseSupplier A Supplier that provides the ResponseEntity when called.
     * @param apiName          The name of the API being called, used for logging.
     * @param logger           The Logger instance to log messages.
     * @param <T>              The type of the response body.
     * @return The ResponseEntity returned by the responseSupplier.
     */
    public <T>ResponseEntity<T> call(Supplier<ResponseEntity<T>> responseSupplier, String apiName, Logger logger) {
        long startTime = System.currentTimeMillis();
        logger.info("API Initiated -> apiName: {}", apiName);
        ResponseEntity<T> responseEntity = responseSupplier.get();
        logger.info("Processing Time ({}) -> {}ms", apiName, System.currentTimeMillis() - startTime);
        return responseEntity;
    }
}
