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

/*
  Developer: Rohit Parihar
  Project: ap-auth-provider
  GitHub: github.com/rohit-zip
  File: RegisterUserValidator
 */

import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.application.exception.BadRequestException;
import com.revquix.sm.application.guardrails.EmailValidator;
import com.revquix.sm.auth.payload.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * RegisterUserValidator is responsible for validating user registration requests.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterUserValidator {

    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;

    private static final Set<String> ALLOWED_VALUES = Set.of("doctor", "user");

    /**
     * Validates the given RegisterRequest object.
     *
     * @param registerRequest the RegisterRequest object to validate
     * @throws BadRequestException if validation fails
     */
    public void validate(RegisterRequest registerRequest) {
        log.info("{} >> validate", getClass().getSimpleName());
        emailValidator.isValidEmail(registerRequest.getEmail(), () -> new BadRequestException(ErrorData.INVALID_EMAIL, "email"));
        passwordValidator.isValid(registerRequest.getPassword());
    }
}
