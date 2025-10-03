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
package com.revquix.sm.application.guardrails;

import com.revquix.sm.application.constants.RegexConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: EmailValidator.java
 */

/**
 * EmailValidator is responsible for validating email addresses.
 */
@Component
@Slf4j
public class EmailValidator {

    /**
     * Validates the given email address.
     *
     * @param email               The email address to validate.
     * @param throwableSupplier   A supplier that provides a RuntimeException to be thrown if validation fails.
     * @throws RuntimeException   If the email address is invalid.
     */
    public void isValidEmail(String email, Supplier<? extends RuntimeException> throwableSupplier) {
        log.info("EmailValidator >> isValidEmail -> {}", email);
        Pattern pattern = Pattern.compile(RegexConstants.EMAIL_REGEX);
        if (!pattern.matcher(email).matches())
            throw throwableSupplier.get();
    }
}
