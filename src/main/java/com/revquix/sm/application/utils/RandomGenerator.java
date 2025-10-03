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

import com.revquix.sm.application.constants.ServiceConstants;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.time.LocalDate;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: RandomGenerator.java
 */

/**
 * RandomGenerator provides utility methods to generate random strings and client secrets.
 */
@UtilityClass
@Slf4j
public class RandomGenerator {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final int ALPHABET_LENGTH = ALPHABET.length();
    private static final int NUMBERS_LENGTH = NUMBERS.length();

    public static String generateRandomString(String characters, int length) {
        log.info("Generating Random String with length {}", length);
        SecureRandom random = new SecureRandom();
        StringBuilder randomString = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            randomString.append(characters.charAt(randomIndex));
        }
        return randomString.toString();
    }

    public static String generateRandomString(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(7);

        for (int i = 0; i < length; i++) {
            boolean isDigit = secureRandom.nextBoolean();
            if (isDigit) {
                int randomIndex = secureRandom.nextInt(NUMBERS_LENGTH);
                stringBuilder.append(NUMBERS.charAt(randomIndex));
            } else {
                int randomIndex = secureRandom.nextInt(ALPHABET_LENGTH);
                stringBuilder.append(ALPHABET.charAt(randomIndex));
            }
        }
        return stringBuilder.toString();
    }

    public static String generateClientSecret() {
        log.info("Generating Client Secret");
        return ServiceConstants.CLIENT_SECRET_PREFIX_STRING +
                "-" +
                generateRandomString(7) +
                "-" +
                generateRandomString(7) +
                "-" +
                LocalDate.now().getMonth().toString();
    }
}
