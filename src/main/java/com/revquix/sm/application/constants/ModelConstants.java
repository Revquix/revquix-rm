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

import lombok.experimental.UtilityClass;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: EntityConstants.java
 */

/**
 * EntityConstants is a utility class that defines constant names for various database
 * schemas and tables used in the application, ensuring consistent naming across the codebase.
 */
@UtilityClass
public class ModelConstants {

    public static final String AUTH_SCHEMA = "auth";
    public static final String USER_AUTH = "user_auth";
    public static final String ROLE_TABLE = "role";
    public static final String REFRESH_TOKEN_TABLE = "refresh_token";
    public static final String CLIENT_TABLE = "client_auth";
    public static final String SCOPE_TABLE = "scope";
    public static final String LOGIN_HISTORY = "login_history";
    public static final String OTP_TABLE = "otp";
    public static final String PROFILE = "profile";
    public static final String PROFILE_SCHEMA = "profile";
    public static final String PROFILE_SEQUENCE = "profile.profile_sequence";
    public static final String USER_UID_SEQUENCE = "auth.user_uid_sequence";
    public static final String USER_PREFERENCE_TABLE = "user_preference";
    public static final String DOCTOR_PROFILE = "doctor_profile";
    public static final String DOCTOR_PROFILE_SEQUENCE = "profile.doctor_profile_sequence";
    public static final String JOURNALING_TABLE_NAME = "journaling";
    public static final String JOURNALING_SEQUENCE = "journaling.journaling_sequence";
    public static final String JOURNALING_SCHEMA = "journaling";
}
