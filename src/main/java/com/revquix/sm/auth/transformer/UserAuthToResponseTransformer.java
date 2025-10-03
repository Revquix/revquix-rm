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
  Project: sana-backend
  GitHub: github.com/rohit-zip
  File: UserAuthToResponseTransformer
 */

import com.revquix.sm.auth.model.UserAuth;
import com.revquix.sm.auth.payload.response.UserAuthResponse;
import com.revquix.sm.application.utils.ModelPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Transformer class to convert UserAuth entity to UserAuthResponse DTO.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserAuthToResponseTransformer extends ModelPayload<UserAuthToResponseTransformer> {

    private final ModelMapper modelMapper;

    /**
     * Transforms a UserAuth entity to a UserAuthResponse DTO.
     *
     * @param userAuth the UserAuth entity to be transformed
     * @return the transformed UserAuthResponse DTO
     */
    public UserAuthResponse transform(UserAuth userAuth) {
        UserAuthResponse userAuthResponse = modelMapper.map(userAuth, UserAuthResponse.class);
        log.info("Transformed UserAuth to UserAuthResponse: {}", userAuthResponse.toJson());
        return userAuthResponse;
    }
}
