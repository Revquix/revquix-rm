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
package com.revquix.sm.auth.processor;

/*
  Developer: Rohit Parihar
  Project: ap-auth-provider
  GitHub: github.com/rohit-zip
  File: SSOUserUpdateDataProcessor
 */

import com.revquix.sm.auth.repository.UserAuthRepository;
import com.revquix.sm.auth.enums.AuthProvider;
import com.revquix.sm.auth.model.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * SSOUserUpdateDataProcessor is responsible for processing and updating user
 * authentication data when a user logs in via Single Sign-On (SSO) providers.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SSOUserUpdateDataProcessor {

    private final UserAuthRepository userAuthRepository;

    /**
     * Processes the UserAuth object by enabling the user if disabled and
     * adding the current AuthProvider to the user's list of authentication providers
     * if it's not already present.
     *
     * @param userAuth        The UserAuth object representing the user's authentication data.
     * @param currentProvider The current AuthProvider used for authentication.
     * @return The updated UserAuth object after processing.
     */
    public UserAuth process(UserAuth userAuth, AuthProvider currentProvider) {
        log.info("{} >> process -> email: {}", getClass().getSimpleName(), userAuth.getEmail());
        if (!userAuth.getIsEnabled()) {
            List<String> authProvider = getProviders(userAuth, currentProvider);
            userAuth.setIsEnabled(true);
            userAuth.setAuthProvider(authProvider);
            userAuth.setDateUpdated(new Date());
            return userAuthRepository.save(userAuth);
        }
        if (!userAuth.getAuthProvider().contains(currentProvider.name())) {
            List<String> providers = getProviders(userAuth, currentProvider);
            userAuth.setAuthProvider(providers);
            userAuth.setDateUpdated(new Date());
            return userAuthRepository.save(userAuth);
        }
        return userAuth;
    }

    /**
     * Retrieves the list of authentication providers for the user and adds
     * the current provider if it's not already present.
     *
     * @param userAuth        The UserAuth object representing the user's authentication data.
     * @param currentProvider The current AuthProvider used for authentication.
     * @return The updated list of authentication providers.
     */
    private static List<String> getProviders(UserAuth userAuth, AuthProvider currentProvider) {
        List<String> authProvider = userAuth.getAuthProvider();
        if (authProvider.isEmpty()) {
            authProvider.add(currentProvider.name());
        } else {
            boolean contains = authProvider.contains(currentProvider.name());
            if (!contains) {
                authProvider.add(currentProvider.name());
            }
        }
        return authProvider;
    }
}
