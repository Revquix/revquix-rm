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

/*
  Developer: Rohit Parihar
  Project: sana-health-backend
  GitHub: github.com/rohit-zip
  File: UserAuthController
 */

import com.revquix.sm.application.payload.ExceptionResponse;
import com.revquix.sm.application.payload.ModuleResponse;
import com.revquix.sm.auth.payload.request.ForgotPasswordRequest;
import com.revquix.sm.auth.payload.request.RegisterRequest;
import com.revquix.sm.auth.service.UserAuthService;
import com.revquix.sm.application.utils.ControllerHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Authentication", description = "Endpoints for user registration, OTP and password flows")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/v1/user-auth")
public class UserAuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthController.class);
    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    /**
     * Endpoint to register a new user.
     *
     * @param registerRequest the request containing user registration details
     * @return ResponseEntity containing the ModuleResponse with registration status
     */
    @Operation(
        summary = "Register a new user",
        description = "Registers a new user with the provided details. Returns registration status.",
        tags = {"User Authentication"},
        operationId = "registerUser",
        responses = {
            @ApiResponse(description = "User registered successfully.", responseCode = "200", content = @Content(
                mediaType = "application/json", schema = @Schema(implementation = ModuleResponse.class)
            )),
            @ApiResponse(description = "Unauthorized access.", responseCode = "401", content = {
                @Content(schema = @Schema(implementation = Void.class))
            }),
            @ApiResponse(description = "Access forbidden.", responseCode = "403", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(description = "Invalid request parameters.", responseCode = "400", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
            })
        },
        security = @SecurityRequirement(name = "bearerAuth", scopes = {"scope.api_register"})
    )
    @PostMapping("/register")
    ResponseEntity<ModuleResponse> registerUser(@RequestBody RegisterRequest registerRequest) {
        return ControllerHelper.call(
                () -> userAuthService.registerUser(registerRequest),
                "Register User",
                LOGGER
        );
    }

    /**
     * Endpoint to authenticate a user registration OTP.
     *
     * @param userId the user ID
     * @param otp    the OTP to authenticate
     * @return ResponseEntity containing the ModuleResponse with authentication status
     */
    @Operation(
        summary = "Authenticate registration OTP",
        description = "Verifies the OTP sent to the user during registration.",
        tags = {"User Authentication"},
        operationId = "authenticateRegisterOtp",
        responses = {
            @ApiResponse(description = "OTP authenticated successfully.", responseCode = "200", content = @Content(
                mediaType = "application/json", schema = @Schema(implementation = ModuleResponse.class)
            )),
            @ApiResponse(description = "Unauthorized access.", responseCode = "401", content = {
                @Content(schema = @Schema(implementation = Void.class))
            }),
            @ApiResponse(description = "Access forbidden.", responseCode = "403", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(description = "Invalid OTP or request parameters.", responseCode = "400", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
            })
        },
        security = @SecurityRequirement(name = "bearerAuth", scopes = {"scope.api_register"})
    )
    @PostMapping(
        value = "/authenticate-otp",
        consumes = "application/x-www-form-urlencoded",
        produces = "application/json"
    )
    ResponseEntity<ModuleResponse> authenticateRegisterOtp(@RequestParam String userId, @RequestParam String otp) {
        return ControllerHelper.call(
                () -> userAuthService.authenticateOtp(userId, otp),
                "Authenticate Register OTP",
                LOGGER
        );
    }

    /**
     * Endpoint to resend the registration OTP to a user.
     *
     * @param userId the user ID to whom the OTP should be resent
     * @return ResponseEntity containing the ModuleResponse with resend status
     */
    @Operation(
        summary = "Resend registration OTP",
        description = "Resends the registration OTP to the specified user.",
        tags = {"User Authentication"},
        operationId = "resendRegisterOtp",
        responses = {
            @ApiResponse(description = "OTP resent successfully.", responseCode = "200", content = @Content(
                mediaType = "application/json", schema = @Schema(implementation = ModuleResponse.class)
            )),
            @ApiResponse(description = "Unauthorized access.", responseCode = "401", content = {
                @Content(schema = @Schema(implementation = Void.class))
            }),
            @ApiResponse(description = "Access forbidden.", responseCode = "403", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(description = "Invalid request parameters.", responseCode = "400", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
            })
        },
        security = @SecurityRequirement(name = "bearerAuth", scopes = {"scope.api_register"})
    )
    @PostMapping("/resend-otp")
    ResponseEntity<ModuleResponse> resendRegisterOtp(@RequestParam String userId) {
        return ControllerHelper.call(
                () -> userAuthService.resendRegisterOtp(userId),
                "Resend Register OTP",
                LOGGER
        );
    }

    /**
     * Endpoint to authenticate a user registration OTP.
     *
     * @param email
     * @return ResponseEntity containing the ModuleResponse with authentication status
     */
    @Operation(
        summary = "Send forgot password OTP",
        description = "Sends a forgot password OTP to the user's email.",
        tags = {"User Authentication"},
        operationId = "forgotPasswordOtp",
        responses = {
            @ApiResponse(description = "OTP sent successfully.", responseCode = "200", content = @Content(
                mediaType = "application/json", schema = @Schema(implementation = ModuleResponse.class)
            )),
            @ApiResponse(description = "Unauthorized access.", responseCode = "401", content = {
                @Content(schema = @Schema())
            }),
            @ApiResponse(description = "Access forbidden.", responseCode = "403", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(description = "Invalid request parameters.", responseCode = "400", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
            })
        }
    )
    @PostMapping(
        value = "/forgot-password-otp"
    )
    ResponseEntity<ModuleResponse> forgotPasswordOtp(@RequestParam String email) {
        return ControllerHelper.call(
                ()-> userAuthService.forgotPasswordOtp(email),
                "Forgot Password OTP",
                LOGGER
        );
    }

    /**
     * Endpoint to reset a user's password.
     *
     * @param forgotPasswordRequest the request containing user email and new password
     * @return ResponseEntity containing the ModuleResponse with reset status
     */
    @Operation(
        summary = "Reset user password",
        description = "Resets the user's password using the provided OTP and new password.",
        tags = {"User Authentication"},
        operationId = "forgotPassword",
        responses = {
            @ApiResponse(description = "Password reset successfully.", responseCode = "200", content = @Content(
                mediaType = "application/json", schema = @Schema(implementation = ModuleResponse.class)
            )),
            @ApiResponse(description = "Unauthorized access.", responseCode = "401", content = {
                @Content(schema = @Schema())
            }),
            @ApiResponse(description = "Access forbidden.", responseCode = "403", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(description = "Invalid request parameters.", responseCode = "400", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
            })
        }
    )
    @PostMapping("/forgot-password")
    ResponseEntity<ModuleResponse> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return ControllerHelper.call(
                ()-> userAuthService.forgotPassword(forgotPasswordRequest),
                "Forgot Password",
                LOGGER
        );
    }

    /**
     * Endpoint to resend the forgot password OTP to a user.
     *
     * @param userId the user ID to whom the OTP should be resent
     * @return ResponseEntity containing the ModuleResponse with resend status
     */
    @Operation(
        summary = "Resend forgot password OTP",
        description = "Resends the forgot password OTP to the specified user.",
        tags = {"User Authentication"},
        operationId = "resendForgotPasswordOtp",
        responses = {
            @ApiResponse(description = "OTP resent successfully.", responseCode = "200", content = @Content(
                mediaType = "application/json", schema = @Schema(implementation = ModuleResponse.class)
            )),
            @ApiResponse(description = "Unauthorized access.", responseCode = "401", content = {
                @Content(schema = @Schema(implementation = Void.class))
            }),
            @ApiResponse(description = "Access forbidden.", responseCode = "403", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(description = "Invalid request parameters.", responseCode = "400", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
            })
        }
    )
    @PostMapping("/resend-forgot-password-otp")
    ResponseEntity<ModuleResponse> resendForgotPasswordOtp(@RequestParam String userId) {
        return ControllerHelper.call(
                () -> userAuthService.resendForgotPasswordOtp(userId),
                "Resend Forgot Password OTP",
                LOGGER
        );
    }

    /**
     * Endpoint to update a user's email address.
     *
     * @param userId
     * @param otp    the OTP to authenticate the email update
     * @return ResponseEntity containing the ModuleResponse with update status
     */
    @Operation(
        summary = "Authenticate forgot password OTP",
        description = "Verifies the OTP sent to the user for password reset.",
        tags = {"User Authentication"},
        operationId = "authenticateForgotPasswordOtp",
        responses = {
            @ApiResponse(description = "OTP authenticated successfully.", responseCode = "200", content = @Content(
                mediaType = "application/json", schema = @Schema(implementation = ModuleResponse.class)
            )),
            @ApiResponse(description = "Unauthorized access.", responseCode = "401", content = {
                @Content(schema = @Schema())
            }),
            @ApiResponse(description = "Access forbidden.", responseCode = "403", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(description = "Invalid OTP or request parameters.", responseCode = "400", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
            })
        }
    )
    @PostMapping(
        value = "/authenticate-forgot-password-otp",
        consumes = "application/x-www-form-urlencoded",
        produces = "application/json"
    )
    ResponseEntity<ModuleResponse> authenticateForgotPasswordOtp(@RequestParam String userId, @RequestParam String otp) {
        return ControllerHelper.call(
                ()-> userAuthService.authenticateForgotPasswordOtp(userId, otp),
                "Authenticate Forgot Password OTP",
                LOGGER
        );
    }
}
