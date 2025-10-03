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
package com.revquix.sm.auth.feign;

/*
  Developer: Rohit Parihar
  Project: ap-auth-provider
  GitHub: github.com/rohit-zip
  File: GoogleFeignClient
 */

import com.revquix.sm.auth.payload.response.GoogleRefreshTokenResponse;
import com.revquix.sm.auth.payload.response.GoogleTokenInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client interface for interacting with Google's OAuth2 API.
 */
@FeignClient(
        name = "googleClient",
        url = "https://oauth2.googleapis.com"
)
public interface GoogleFeignClient {

    @PostMapping(
            value = "/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    GoogleRefreshTokenResponse exchangeAuthCodeForToken(@RequestBody MultiValueMap<String, String> formParams);

    @GetMapping("/tokeninfo")
    GoogleTokenInfoResponse tokenInfo(@RequestParam("access_token") String accessToken);

    @PostMapping(
            value = "/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    GoogleRefreshTokenResponse refreshToken(@RequestBody MultiValueMap<String, String> formParams);
}
