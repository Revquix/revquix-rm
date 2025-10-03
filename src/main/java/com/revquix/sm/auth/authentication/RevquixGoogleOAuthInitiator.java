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

/*
  Developer: Rohit Parihar
  Project: ap-auth-provider
  GitHub: github.com/rohit-zip
  File: BloggiosGoogleOAuthInitiator
 */

import com.revquix.sm.auth.feign.GoogleFeignClient;
import com.revquix.sm.auth.payload.response.GoogleRefreshTokenResponse;
import com.revquix.sm.auth.payload.response.GoogleTokenInfoResponse;
import com.revquix.sm.auth.properties.AuthenticationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * SanaGoogleOAuthInitiator is responsible for handling Google OAuth operations such as
 * exchanging authorization codes for tokens, refreshing tokens, and retrieving token information.
 * It interacts with the GoogleFeignClient to communicate with Google's OAuth services.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RevquixGoogleOAuthInitiator {

    private final GoogleFeignClient googleFeignClient;
    private final AuthenticationProperties authenticationProperties;

    public GoogleTokenInfoResponse getTokenInfo(String accessToken) {
        return googleFeignClient.tokenInfo(accessToken);
    }

    public GoogleRefreshTokenResponse refreshToken(String refreshToken) {
        AuthenticationProperties.Google google = authenticationProperties.getOauth().getClients().getGoogle();
        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add("client_id", google.getClientId());
        formParams.add("client_secret", google.getClientSecret());
        formParams.add("grant_type", "refresh_token");
        formParams.add("refresh_token", refreshToken);
        return googleFeignClient.refreshToken(formParams);
    }
}
