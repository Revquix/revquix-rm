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

import com.revquix.sm.application.constants.RegexConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: UsernameValidator.java
 */

/**
 * UsernameValidator is responsible for validating usernames.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UsernameValidator {

    /**
     * Validates the given username.
     *
     * @param username           The username to validate.
     * @param throwableSupplier  A supplier that provides a RuntimeException to be thrown if validation fails.
     * @throws RuntimeException  If the username is invalid.
     */
    public void isValidUsername(String username, Supplier<? extends RuntimeException> throwableSupplier) {
        log.info("UsernameValidator >> isValidUsername -> {}", username);
        if (username.length() < 4)
            throw throwableSupplier.get();
        Pattern pattern = Pattern.compile(RegexConstants.USERNAME_REGEX);
        if (!pattern.matcher(username.toLowerCase().trim()).matches())
            throw throwableSupplier.get();
    }
}
