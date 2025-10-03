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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: CookieUtils.java
 */

/**
 * CookieUtils provides utility methods for handling cookies in HTTP requests.
 */
@UtilityClass
@Slf4j
public class CookieUtils {

    /**
     * Retrieves a cookie by name from the given HttpServletRequest.
     *
     * @param request The HttpServletRequest containing the cookies.
     * @param name    The name of the cookie to retrieve.
     * @return An Optional containing the Cookie if found, or empty if not found.
     */
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        log.info("CookieUtils >> getCookie -> {}", name);
        Cookie[] cookies = request.getCookies();
        if (Objects.nonNull(cookies) && cookies.length > 0) {
            return Arrays
                    .stream(cookies)
                    .filter(cookie -> Objects.equals(cookie.getName(), name))
                    .findFirst();
        }
        return Optional.empty();
    }
}
