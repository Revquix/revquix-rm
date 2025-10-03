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
  File: DoLoginOrRegisterSSO
 */

import com.revquix.sm.auth.repository.UserAuthRepository;
import com.revquix.sm.auth.enums.AuthProvider;
import com.revquix.sm.auth.model.UserAuth;
import com.revquix.sm.auth.payload.response.FacebookUserDetailsResponse;
import com.revquix.sm.auth.transformer.FacebookUserAuthTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Processor for registering or updating a user using Facebook SSO details.
 * <p>
 * This class checks if a user with the provided email already exists. If not, it registers a new user.
 * If the user exists, it updates their information using the SSO data processor.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FacebookRegisterUser {

    private final UserAuthRepository userAuthRepository;
    private final FacebookUserAuthTransformer facebookUserAuthTransformer;
    private final SSOUserUpdateDataProcessor ssoUserUpdateDataProcessor;

    /**
     * Processes the Facebook user details to either register a new user or update an existing one.
     *
     * @param facebookUserDetailsResponse The Facebook user details response containing user information.
     * @return The registered or updated UserAuth object.
     */
    public UserAuth process(FacebookUserDetailsResponse facebookUserDetailsResponse) {
        log.info("{} >> process", getClass().getSimpleName());
        Optional<UserAuth> userAuthByEmailOptional = userAuthRepository.findByEmail(facebookUserDetailsResponse.getEmail());
        if (userAuthByEmailOptional.isEmpty()) {
            return doRegister(facebookUserDetailsResponse);
        } else {
            log.info("{} >> Found existing user", getClass().getSimpleName());
            return ssoUserUpdateDataProcessor.process(userAuthByEmailOptional.get(), AuthProvider.facebook);
        }
    }

    /**
     * Registers a new user using the provided Facebook user details.
     *
     * @param facebookUserDetailsResponse The Facebook user details response containing user information.
     * @return The newly registered UserAuth object.
     */
    private UserAuth doRegister(FacebookUserDetailsResponse facebookUserDetailsResponse) {
        String email = facebookUserDetailsResponse.getEmail();
        UserAuth userAuth = facebookUserAuthTransformer.transform(facebookUserDetailsResponse);
        UserAuth userAuthResponse = userAuthRepository.save(userAuth);
        log.info("{} >> created new user -> userId: {}, email: {}", getClass().getSimpleName(), userAuthResponse.getUserId(), email);
        return userAuthResponse;
    }
}
