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
package com.revquix.sm.auth.payload.response;

/*
  Developer: Rohit Parihar
  Project: ap-auth-provider
  GitHub: github.com/rohit-zip
  File: GoogleRefreshTokenResponse
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Schema(name = "GoogleRefreshTokenResponse", description = "Response returned by Google when exchanging/refreshing tokens")
public class GoogleRefreshTokenResponse {

    @Schema(description = "Access token returned by Google", example = "ya29.a0AfH6SMD...")
    @JsonProperty("access_token")
    private String accessToken;

    @Schema(description = "Refresh token returned by Google (may be omitted)", example = "1//0g...")
    @JsonProperty("refresh_token")
    private String refreshToken;

    @Schema(description = "Scopes granted by Google", example = "openid email profile")
    @JsonProperty("scope")
    private String scope;

    @Schema(description = "ID token returned by Google (JWT)", example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...")
    @JsonProperty("id_token")
    private String idToken;
}
