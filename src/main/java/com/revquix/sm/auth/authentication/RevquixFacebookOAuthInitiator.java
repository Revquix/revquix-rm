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
  File: BloggiosFacebookOauthInitiator
 */

import com.revquix.sm.auth.feign.FacebookFeignClient;
import com.revquix.sm.auth.payload.request.FacebookOauthRequest;
import com.revquix.sm.auth.payload.response.FacebookLongLiveTokenResponse;
import com.revquix.sm.auth.payload.response.FacebookTokenValidationResponse;
import com.revquix.sm.auth.payload.response.FacebookUserDetailsResponse;
import com.revquix.sm.auth.properties.AuthenticationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SanaFacebookOAuthInitiator is responsible for handling Facebook OAuth operations such as
 * validating access tokens, exchanging long-lived tokens, and retrieving user details.
 * It interacts with the FacebookFeignClient to communicate with Facebook's OAuth services.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RevquixFacebookOAuthInitiator {

    private final AuthenticationProperties authenticationProperties;
    private final FacebookFeignClient facebookFeignClient;

    public boolean isValidAccessToken(FacebookOauthRequest facebookOauthRequest) {
        AuthenticationProperties.Facebook facebook = authenticationProperties.getOauth().getClients().getFacebook();
        String appAccessToken = facebook.getAppId() + "|" + facebook.getAppSecret();
        FacebookTokenValidationResponse tokenResponse = facebookFeignClient.decodeToken(facebookOauthRequest.getAccessToken(), appAccessToken);
        FacebookTokenValidationResponse.FacebookTokenValidationData data = tokenResponse.getData();
        return data.isValid();
    }

    public FacebookLongLiveTokenResponse exchangeLongLiveToken(FacebookOauthRequest facebookOauthRequest) {
        AuthenticationProperties.Facebook facebook = authenticationProperties.getOauth().getClients().getFacebook();
        return facebookFeignClient.exchangeLongLivedToken("fb_exchange_token", facebook.getAppId(), facebook.getAppSecret(), facebookOauthRequest.getAccessToken());
    }

    public FacebookUserDetailsResponse getUserDetails(String accessToken) {
        return facebookFeignClient.getUserDetails("id,name,email", accessToken);
    }
}
