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

import com.revquix.sm.auth.repository.UserAuthRepository;
import com.revquix.sm.auth.enums.EntrypointType;
import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.application.exception.AuthenticationException;
import com.revquix.sm.auth.model.UserAuth;
import com.revquix.sm.auth.utils.EntrypointTypeUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: BloggiosUserDetailsService.java
 */

/**
 * SanaUserDetailsService implements UserDetailsService to load user-specific data during authentication.
 * It retrieves user information from the UserAuthRepository based on the provided username,
 * which can be an email, username, or mobile number.
 */
@Service
public class RevquixUserDetailsService implements UserDetailsService {

    private final UserAuthRepository userAuthRepository;

    public RevquixUserDetailsService(UserAuthRepository userAuthRepository) {
        this.userAuthRepository = userAuthRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EntrypointType entrypointType = EntrypointTypeUtil.parseEntrypoint(username);
        ErrorData errorData;
        if (entrypointType.equals(EntrypointType.email)) errorData = ErrorData.NO_USER_WITH_EMAIL;
        else if (entrypointType.equals(EntrypointType.username)) errorData = ErrorData.NO_USER_WITH_USERNAME;
        else errorData = ErrorData.NO_USER_WITH_MOBILE;
        UserAuth userAuth = userAuthRepository.findByEmailOrUsernameOrMobile(username)
                .orElseThrow(() -> new AuthenticationException(errorData, HttpStatus.UNAUTHORIZED));
        return UserPrincipal.create(userAuth);
    }
}
