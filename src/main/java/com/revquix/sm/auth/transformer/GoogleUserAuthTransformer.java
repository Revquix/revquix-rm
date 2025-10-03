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
package com.revquix.sm.auth.transformer;

/*
  Developer: Rohit Parihar
  Project: ap-auth-provider
  GitHub: github.com/rohit-zip
  File: GoogleUserAuthTransformer
 */

import com.revquix.sm.auth.repository.RoleRepository;
import com.revquix.sm.auth.enums.AuthProvider;
import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.application.exception.InternalServerException;
import com.revquix.sm.auth.model.Role;
import com.revquix.sm.auth.model.UserAuth;
import com.revquix.sm.auth.payload.response.GoogleTokenInfoResponse;
import com.revquix.sm.auth.utils.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Transformer class to convert Google user details response into a UserAuth entity.
 * <p>
 * This class handles the transformation of data received from Google's token info
 * API into the internal UserAuth model used for authentication and authorization.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleUserAuthTransformer {

    private final RoleRepository roleRepository;
    private final UsernameGenerator usernameGenerator;

    /**
     * Transforms the GoogleTokenInfoResponse into a UserAuth entity.
     *
     * @param facebookUserDetailsResponse The response containing user details from Google.
     * @return The transformed UserAuth entity.
     * @throws InternalServerException if the default role is not found in the database.
     */
    public UserAuth transform(GoogleTokenInfoResponse facebookUserDetailsResponse) {
        log.info("{} >> Register google user -> email: {}", getClass().getSimpleName(), facebookUserDetailsResponse.getEmail());
        List<Role> roles = new ArrayList<>();
        roleRepository.findById("user")
                .ifPresentOrElse(
                        roles::add,
                        () -> {
                            throw new InternalServerException(ErrorData.ROLE_NOT_FOUND_BY_ID);
                        });
        return UserAuth
                .builder()
                .email(facebookUserDetailsResponse.getEmail().toLowerCase())
                .username(usernameGenerator.generate(facebookUserDetailsResponse.getEmail()).toLowerCase())
                .isEnabled(Boolean.TRUE)
                .isAccountNonLocked(Boolean.TRUE)
                .authProvider(List.of(AuthProvider.google.name()))
                .dateCreated(new Date())
                .dateUpdated(new Date())
                .roles(roles)
                .build();
    }
}
