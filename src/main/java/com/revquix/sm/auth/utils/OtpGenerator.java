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

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.function.Supplier;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: OtpGenerator.java
 */

/**
 * OtpGenerator provides a method to generate a 4-digit OTP (One-Time Password).
 */
@UtilityClass
public class OtpGenerator {

    public static final Supplier<String> generateOtp = () -> {
        StringBuilder string = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i=0 ; i<4 ; i++){
            string.append(secureRandom.nextInt(9));
        }
        return string.toString();
    };
}
