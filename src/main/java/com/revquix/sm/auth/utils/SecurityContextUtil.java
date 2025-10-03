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

import com.revquix.sm.auth.authentication.AuthIdentity;
import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.application.exception.AuthenticationException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: SecurityContextUtil.java
 */

/**
 * SecurityContextUtil provides utility methods to retrieve the currently logged-in user's
 * authentication details from the security context.
 */
@UtilityClass
@Slf4j
public class SecurityContextUtil {

    /**
     * Retrieves the currently logged-in user's AuthIdentity from the security context.
     *
     * @return AuthIdentity of the logged-in user.
     * @throws AuthenticationException if no user is logged in or if the authentication details are invalid.
     */
    public static AuthIdentity getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.getPrincipal() instanceof AuthIdentity authIdentity) {
            log.info("Request coming from userId: {}", authIdentity.getUserId());
            return authIdentity;
        }
        log.info("Request coming from unauthenticated user");
        return null;
    }

    /**
     * Retrieves the currently logged-in user's authentication details.
     * Throws an AuthenticationException if no user is logged in.
     *
     * @return AuthIdentity of the logged-in user
     * @throws AuthenticationException if no user is logged in
     */
    public static AuthIdentity getLoggedInUserOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.getPrincipal() instanceof AuthIdentity authIdentity) {
            log.info("Request coming from userId: {}", authIdentity.getUserId());
            return authIdentity;
        }
        log.info("Request coming from unauthenticated user -> Throwing Exception");
        throw new AuthenticationException(ErrorData.USER_NOT_LOGGED_IN);
    }
}
