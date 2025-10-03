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
package com.revquix.sm.application.constants;

/*
  Developer: Rohit Parihar
  Project: ap-auth-provider
  GitHub: github.com/rohit-zip
  File: RegexConstants
 */

import lombok.experimental.UtilityClass;

/**
 * RegexConstants is a utility class that defines common regular expression patterns
 * used for validating inputs such as passwords, mobile numbers, emails, usernames,
 * and names across the application.
 */
@UtilityClass
public class RegexConstants {

    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,}$";
    public static final String UNIVERSAL_MOBILE_REGEX = "^\\+?[1-9]\\d{1,3}?[-.\\s]?\\d{1,4}[-.\\s]?\\d{4,10}$";
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final String USERNAME_REGEX = "^[a-z][a-z0-9_-]*$";
    public static final String NAME_REGEX = "^[a-zA-Z\\s'-]{3,50}$";
}
