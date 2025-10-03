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
  Project: bloggios-matching
  GitHub: github.com/rohit-zip
  File: WordsCounter
 */

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * WordsCounter provides a utility method to count the number of words in a given string.
 */
@UtilityClass
@Slf4j
public class WordsCounter {

    /**
     * Counts the number of words in the input string. Words are defined as sequences of characters
     * separated by whitespace.
     *
     * @param input The input string to count words in.
     * @return The number of words in the input string.
     */
    public static int countWords(String input) {
        log.info("{} >> countWords -> input: {}", WordsCounter.class.getSimpleName(), input);
        if (input == null || input.isEmpty()) {
            return 0;
        }
        String[] words = input.split("\\s+");
        return (int) Arrays.stream(words)
                .filter(word -> !word.isEmpty())
                .count();
    }
}
