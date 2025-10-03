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
package com.revquix.sm.auth.authentication;

import com.revquix.sm.auth.payload.record.LoginCredentials;
import com.revquix.sm.auth.payload.record.LoginPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: AuthenticationProvider.java
 */

/**
 * AuthenticationProvider is a custom authentication provider that extends DaoAuthenticationProvider.
 * It uses AuthenticateHelper to generate authentication tokens based on the provided principal
 * and credentials.
 */
@Slf4j
public class AuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private AuthenticateHelper authenticateHelper;

    @Autowired
    private RevquixUserDetailsService revquixUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LoginPrincipal principal = (LoginPrincipal) authentication.getPrincipal();
        LoginCredentials credentials = (LoginCredentials) authentication.getCredentials();
        return authenticateHelper.generateAuthentication(principal, credentials);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(RevquixAuthenticationToken.class);
    }
}
