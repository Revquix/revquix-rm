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
  Project: bloggios-auth-server
  GitHub: github.com/rohit-zip
  File: LoginHistoryProcessor
 */

import com.revquix.sm.auth.repository.LoginHistoryRepository;
import com.revquix.sm.auth.enums.AuthProvider;
import com.revquix.sm.auth.feign.IPAPIFeign;
import com.revquix.sm.auth.model.LoginHistory;
import com.revquix.sm.auth.payload.response.AuthResponse;
import com.revquix.sm.auth.payload.response.IPResponse;
import com.revquix.sm.application.utils.IpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;

/**
 * LoginHistoryProcessor is responsible for processing and saving login history
 * information, including IP address details and country information.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoginHistoryProcessor {

    private final IPAPIFeign ipapiFeign;
    private final LoginHistoryRepository loginHistoryRepository;
    private final IpUtils ipUtils;

    /**
     * Processes the login history by retrieving IP address details and saving
     * the login information to the database.
     *
     * @param authResponse       The authentication response containing user details.
     * @param authProvider       The authentication provider used for login.
     */
    public void process(AuthResponse authResponse, AuthProvider authProvider) {
        log.info("{} >> process -> Processing login history, email: {}", getClass().getSimpleName(), authResponse.getEmail());
        String remoteAddress = ipUtils.getRemoteAddress();
        log.info("{} >> process -> Calling External API to get details of IP Address, ipAddress: {}", getClass().getSimpleName(), remoteAddress);
        String countryName = "";
        if (StringUtils.hasText(remoteAddress)) {
            IPResponse ipDetails = ipapiFeign.getDetails(remoteAddress);
            log.info("{} >> process -> IP Details: {}", getClass().getSimpleName(), ipDetails);
            if (Objects.nonNull(ipDetails) && ipDetails.getStatus().equalsIgnoreCase("success")) {
                countryName = ipDetails.getCountry();
            } else {
                log.warn("{} >> process -> Error retrieving details of IP Address", getClass().getSimpleName());
            }
        }
        LoginHistory loginHistory = LoginHistory
                .builder()
                .userId(authResponse.getUserId())
                .dateCreated(new Date())
                .provider(authProvider.name())
                .entryPoint(authResponse.getEmail())
                .ipAddress(remoteAddress)
                .country(countryName)
                .build();
        LoginHistory loginHistoryResponse = loginHistoryRepository.save(loginHistory);
        log.info("{} >> process -> Login history saved to Database : {}", getClass().getSimpleName(), loginHistoryResponse);
    }
}
