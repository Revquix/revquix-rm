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
package com.revquix.sm.auth.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/*
  Developer: Rohit Parihar
  Project: ap-auth-provider
  GitHub: github.com/rohit-zip
  File: FacebookOauthRequest
 */


@Schema(description = "Request payload for Facebook OAuth authentication")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacebookOauthRequest {

    @Schema(description = "Facebook OAuth access token",
            example = "EAAJz...k2lC",
            required = true)
    private String accessToken;

    @Schema(description = "Facebook OAuth client ID",
            example = "12345678901234567",
            required = true)
    private String clientId;

    @Schema(description = "Facebook OAuth client secret",
            example = "abc123xyz456...",
            required = true)
    private String clientSecret;
}