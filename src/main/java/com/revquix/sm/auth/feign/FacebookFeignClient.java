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
  File: FacebookFeignClient
 */

import com.revquix.sm.auth.payload.response.FacebookLongLiveTokenResponse;
import com.revquix.sm.auth.payload.response.FacebookTokenValidationResponse;
import com.revquix.sm.auth.payload.response.FacebookUserDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client interface for interacting with Facebook's Graph API.
 */
@FeignClient(
        name = "facebookClient",
        url = "https://graph.facebook.com/v20.0"
)
public interface FacebookFeignClient {

    @GetMapping("/debug_token")
    FacebookTokenValidationResponse decodeToken(
            @RequestParam("input_token") String accessToken,
            @RequestParam("access_token") String appAccessToken
    );

    @GetMapping("/oauth/access_token")
    FacebookLongLiveTokenResponse exchangeLongLivedToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String appId,
            @RequestParam("client_secret") String appSecret,
            @RequestParam("fb_exchange_token") String shortLivedToken
    );

    @GetMapping("/me")
    FacebookUserDetailsResponse getUserDetails(
            @RequestParam("fields") String fields,
            @RequestParam("access_token") String accessToken
    );
}
