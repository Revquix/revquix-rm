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

import com.revquix.sm.application.constants.RegexConstants;
import com.revquix.sm.auth.enums.EntrypointType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * 
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: EntrypointTypeUtil.java
 */

/**
 * EntrypointTypeUtil provides utility methods for determining the type of an entry point
 * such as email, mobile number, or username.
 */
@UtilityClass
@Slf4j
public class EntrypointTypeUtil {

    public EntrypointType parseEntrypoint(String entrypoint) {
        log.info("Entry Point Util >> Parsing Entry Point -> {}", entrypoint);
        if (entrypoint.contains("@") && entrypoint.contains("."))
            return EntrypointType.email;
        else if (Pattern.matches(RegexConstants.UNIVERSAL_MOBILE_REGEX, entrypoint))
            return EntrypointType.mobile;
        else
            return EntrypointType.username;
    }
}
