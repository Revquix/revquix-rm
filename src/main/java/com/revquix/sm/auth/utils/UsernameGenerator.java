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
package com.revquix.sm.auth.utils;

import com.revquix.sm.application.utils.RandomGenerator;
import com.revquix.sm.auth.repository.UserAuthRepository;
import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.application.exception.InternalServerException;
import com.revquix.sm.auth.model.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: UsernameGenerator.java
 */

/**
 * Utility class for generating unique usernames based on email addresses.
 * This class ensures that the generated username does not conflict with existing usernames
 * in the system by appending random characters if necessary.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UsernameGenerator {

    private final UserAuthRepository userAuthRepository;

    /**
     * Generates a unique username based on the provided email address.
     * The username is derived from the prefix of the email (before the '@' symbol)
     * and is sanitized to remove special characters. If the initial username
     * already exists, random characters are appended until a unique username is found.
     *
     * @param email The email address to base the username on.
     * @return A unique username.
     * @throws InternalServerException if unable to generate a unique username after several attempts.
     */
    public String generate(String email) {
        log.info("Generating Username for reference email : {}", email);
        int atIndex = email.lastIndexOf("@");
        String prefixData = email.substring(0, atIndex);
        String initialData = removeSpecialCharacters(prefixData);
        boolean isPresent = true;
        String username = initialData;
        Optional<UserAuth> byUsername = userAuthRepository.findByUsername(username);
        if (byUsername.isEmpty()) {
            isPresent = false;
        }

        int round = 0;
        while (isPresent) {
            if (round == 7) throw new InternalServerException(ErrorData.UNABLE_TO_GENERATE_USERNAME);
            if (round == 0) {
                log.warn("Username Generator (Round 0)");
                String characters = "0123456789";
                username = username + RandomGenerator.generateRandomString(characters, 1);
            } else if (round == 1) {
                log.warn("Username Generator (Round 1)");
                String characters = "0123456789abcdefghijklmnopqrstuvwxyz";
                username = username + RandomGenerator.generateRandomString(characters, 2);
            } else if (round >= 2) {
                log.warn("Username Generator (Round {})", round);
                String characters = "0123456789abcdefghijklmnopqrstuvwxyz-_";
                username = username + RandomGenerator.generateRandomString(characters, round*2);
            }
            Optional<UserAuth> userDocumentOptional = userAuthRepository.findByUsername(username);
            if (userDocumentOptional.isEmpty()) {
                isPresent = false;
            } else {
                round++;
            }
        }
        return username;
    }

    /**
     * Removes special characters from the given string, allowing only alphanumeric characters,
     * underscores, and hyphens.
     *
     * @param data The input string to sanitize.
     * @return The sanitized string with special characters removed.
     */
    public static String removeSpecialCharacters(String data) {
        return data.replaceAll("[^a-zA-Z0-9_-]", "");
    }
}
