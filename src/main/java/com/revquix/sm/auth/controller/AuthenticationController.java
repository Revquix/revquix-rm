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
package com.revquix.sm.auth.controller;

import com.revquix.sm.application.payload.ExceptionResponse;
import com.revquix.sm.auth.payload.request.AuthenticateRequest;
import com.revquix.sm.auth.payload.request.FacebookOauthRequest;
import com.revquix.sm.auth.payload.request.GoogleOAuthRequest;
import com.revquix.sm.auth.payload.response.AuthResponse;
import com.revquix.sm.auth.service.AuthenticationService;
import com.revquix.sm.application.utils.ControllerHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: AuthenticationApi.java
 */

@Tag(name = "Authentication", description = "Authentication and OAuth endpoints")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Authenticate user and issue tokens",
            description = "Authenticate the user using provided entrypoint (email/phone), password and client credentials. Returns access and refresh tokens in response.",
            tags = {"Authentication"},
            operationId = "authenticateUser",
            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)
                    )),
                    @ApiResponse(description = "Unauthorized - invalid credentials", responseCode = "401", content = {
                            @Content(schema = @Schema())
                    }),
                    @ApiResponse(description = "Forbidden - client not allowed", responseCode = "403", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    }),
                    @ApiResponse(description = "Bad Request - validation errors", responseCode = "400", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
                    })
            }
    )
    @PostMapping(
            value = "/authenticate",
            consumes = "application/x-www-form-urlencoded",
            produces = "application/json"
    )
    ResponseEntity<AuthResponse> authenticate(
            @RequestParam(required = false) String entrypoint,
            @RequestParam(required = false) String password,
            @RequestParam String clientId,
            @RequestParam String clientSecret,
            HttpServletRequest httpServletRequest
    ) {
        return ControllerHelper.call(
                () -> authenticationService.authenticate(new AuthenticateRequest(entrypoint, password, clientId, clientSecret), httpServletRequest),
                "Authenticate",
                LOGGER
        );
    }

    @Operation(
            summary = "Refresh access token",
            description = "Refreshes the access token using the refresh token supplied in the request (cookie/header). Returns a new access token.",
            tags = {"Authentication"},
            operationId = "refreshAccessToken",
            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)
                    )),
                    @ApiResponse(description = "Unauthorized - invalid or expired refresh token", responseCode = "401", content = {
                            @Content(schema = @Schema())
                    }),
                    @ApiResponse(description = "Forbidden - client not allowed", responseCode = "403", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    }),
                    @ApiResponse(description = "Bad Request - validation errors", responseCode = "400", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
                    })
            }
    )
    @GetMapping("/refresh-token")
    ResponseEntity<AuthResponse> refreshToken(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {
        return ControllerHelper.call(
                ()-> authenticationService.refreshToken(httpServletRequest, httpServletResponse),
                "Refresh Token",
                LOGGER
        );
    }

    @Operation(
            summary = "Authenticate/Signup via Facebook OAuth",
            description = "Accepts Facebook OAuth payload and returns application tokens after validating Facebook credentials.",
            tags = {"Authentication","OAuth"},
            operationId = "facebookOauth",
            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)
                    )),
                    @ApiResponse(description = "Unauthorized - invalid oauth token", responseCode = "401", content = {
                            @Content(schema = @Schema())
                    }),
                    @ApiResponse(description = "Forbidden - client not allowed", responseCode = "403", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    }),
                    @ApiResponse(description = "Bad Request - validation errors", responseCode = "400", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
                    })
            }
    )
    @PostMapping("/facebook-oauth")
    public ResponseEntity<AuthResponse> facebookOauth(FacebookOauthRequest facebookOauthRequest, HttpServletRequest httpServletRequest) {
        return ControllerHelper.call(
                ()-> authenticationService.facebookOauth(facebookOauthRequest, httpServletRequest),
                "Facebook OAuth",
                LOGGER
        );
    }

    @Operation(
            summary = "Authenticate/Signup via Google OAuth",
            description = "Accepts Google OAuth payload and returns application tokens after validating Google credentials.",
            tags = {"Authentication","OAuth"},
            operationId = "googleOauth",
            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)
                    )),
                    @ApiResponse(description = "Unauthorized - invalid oauth token", responseCode = "401", content = {
                            @Content(schema = @Schema())
                    }),
                    @ApiResponse(description = "Forbidden - client not allowed", responseCode = "403", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    }),
                    @ApiResponse(description = "Bad Request - validation errors", responseCode = "400", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
                    })
            }
    )
    @PostMapping("/google-oauth")
    public ResponseEntity<AuthResponse> googleOauth(@RequestBody GoogleOAuthRequest googleOAuthRequest, HttpServletRequest httpServletRequest) {
        return ControllerHelper.call(
                ()-> authenticationService.googleOauth(googleOAuthRequest, httpServletRequest),
                "Google OAuth",
                LOGGER
        );
    }

    @Operation(
            summary = "Logout user and revoke tokens",
            description = "Logs out the current user and revokes any active tokens (access/refresh).",
            tags = {"Authentication"},
            operationId = "logout",
            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)
                    )),
                    @ApiResponse(description = "Unauthorized - invalid token", responseCode = "401", content = {
                            @Content(schema = @Schema())
                    }),
                    @ApiResponse(description = "Forbidden - client not allowed", responseCode = "403", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    }),
                    @ApiResponse(description = "Bad Request - validation errors", responseCode = "400", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
                    })
            }
    )
    @GetMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return ControllerHelper.call(
                ()-> authenticationService.logout(httpServletRequest, httpServletResponse),
                "Logout",
                LOGGER
        );
    }
}
