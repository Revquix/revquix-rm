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

import com.revquix.sm.application.constants.ServiceConstants;
import com.revquix.sm.application.utils.IpUtils;
import com.revquix.sm.auth.repository.RoleRepository;
import com.revquix.sm.auth.enums.AuthProvider;
import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.application.exception.InternalServerException;
import com.revquix.sm.auth.model.Role;
import com.revquix.sm.auth.model.UserAuth;
import com.revquix.sm.auth.payload.request.RegisterRequest;
import com.revquix.sm.auth.utils.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: RegisterRequestToUserAuthTransformer.java
 */

/**
 * Transformer class to convert RegisterRequest into a UserAuth entity.
 *
 * This class handles the transformation of data received from user registration
 * requests into the internal UserAuth model used for authentication and authorization.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterRequestToUserAuthTransformer {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UsernameGenerator usernameGenerator;
    private final IpUtils ipUtils;

    /**
     * Transforms the RegisterRequest into a UserAuth entity.
     * This method assigns the default 'user' role to the user. If the email belongs to a super domain,
     * it assigns all available roles to the user.
     *
     * @param registerRequest The registration request containing user details.
     * @return The transformed UserAuth entity.
     * @throws InternalServerException if the default role is not found in the database.
     */
    public UserAuth transform(RegisterRequest registerRequest) {
        List<Role> roles = new ArrayList<>();
        if (registerRequest.getEmail().contains(ServiceConstants.SUPER_DOMAIN)) {
            roles.addAll(roleRepository.findAll());
        } else {
            roleRepository.findById("user")
                    .ifPresentOrElse(
                            roles::add,
                            () -> {
                                throw new InternalServerException(ErrorData.ROLE_NOT_FOUND_BY_ID);
                            });
        }
        UserAuth userAuth = UserAuth
                .builder()
                .email(registerRequest.getEmail().toLowerCase())
                .username(usernameGenerator.generate(registerRequest.getEmail()).toLowerCase())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .isEnabled(Boolean.FALSE)
                .isAccountNonLocked(Boolean.TRUE)
                .authProvider(List.of(AuthProvider.email.name()))
                .dateCreated(new Date())
                .dateUpdated(new Date())
                .registerIp(ipUtils.getRemoteAddress())
                .roles(roles)
                .build();
        log.info("Transformed User Auth : {}", userAuth);
        return userAuth;
    }
}
