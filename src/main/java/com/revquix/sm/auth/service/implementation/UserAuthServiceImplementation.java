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
package com.revquix.sm.auth.service.implementation;

/*
  Developer: Rohit Parihar
  Project: sana-health-backend
  GitHub: github.com/rohit-zip
  File: UserAuthServiceImplementation
 */

import com.revquix.sm.auth.repository.OtpEntityRepository;
import com.revquix.sm.auth.repository.RoleRepository;
import com.revquix.sm.auth.repository.UserAuthRepository;
import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.auth.enums.OtpFor;
import com.revquix.sm.auth.enums.OtpStatus;
import com.revquix.sm.auth.events.*;
import com.revquix.sm.application.exception.BadRequestException;
import com.revquix.sm.auth.model.OtpEntity;
import com.revquix.sm.auth.model.UserAuth;
import com.revquix.sm.application.payload.ModuleResponse;
import com.revquix.sm.auth.payload.request.ForgotPasswordRequest;
import com.revquix.sm.auth.payload.request.RegisterRequest;
import com.revquix.sm.auth.service.UserAuthService;
import com.revquix.sm.auth.transformer.RegisterRequestToUserAuthTransformer;
import com.revquix.sm.application.utils.ValueCheckerUtil;
import com.revquix.sm.auth.guardrails.RegisterUserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * Implementation of UserAuthService for managing user authentication and registration.
 * <p>
 * This service provides methods to register users, authenticate OTPs, resend OTPs,
 * handle forgot password requests, and update user passwords. It includes validation,
 * transformation, and interaction with the database repository.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthServiceImplementation implements UserAuthService {

    private final RegisterUserValidator registerUserValidator;
    private final UserAuthRepository userAuthRepository;
    private final RegisterRequestToUserAuthTransformer registerRequestToUserAuthTransformer;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final OtpEntityRepository otpEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    /**
     * Registers a new user by validating the registration request, checking if the user already exists,
     * and saving the user authentication data to the database. If the user is successfully registered,
     * an event is published to send an OTP for verification.
     *
     * @param registerRequest The registration request containing user details.
     * @return ResponseEntity containing the module response with success message and user ID.
     */
    @Override
    @Transactional
    public ResponseEntity<ModuleResponse> registerUser(RegisterRequest registerRequest) {
        registerUserValidator.validate(registerRequest);
        Optional<UserAuth> userAuthOptional = userAuthRepository.findByEmail(registerRequest.getEmail().toLowerCase());
        if (userAuthOptional.isPresent()) {
            UserAuth userAuth = userAuthOptional.get();
            if (Boolean.TRUE.equals(userAuth.getIsEnabled())) {
                throw new BadRequestException(ErrorData.USER_ALREADY_REGISTERED);
            } else {
                log.info("{} >> registerUser -> Deleting User Auth Data as user not enabled and getting new request for register", getClass().getSimpleName());
                userAuthRepository.delete(userAuth);
            }
        }
        UserAuth userAuth = registerRequestToUserAuthTransformer.transform(registerRequest);
        UserAuth userAuthResponse = userAuthRepository.save(userAuth);
        log.info("User Auth saved to Database : {}", userAuthResponse.toString());
        applicationEventPublisher.publishEvent(new UserRegistrationOtpEvent(userAuthResponse));
        return ResponseEntity.ok(ModuleResponse
                .builder()
                .message("User Registered Successfully")
                .userId(userAuthResponse.getUserId().toString())
                .build());
    }

    /**
     * Authenticates the user by checking if the provided email and password match an existing user.
     * If the user is found and enabled, a response is returned with the user ID.
     *
     * @param userId    The userId of the user.
     * @param otp The OTP.
     * @return ResponseEntity containing the module response with success message and user ID.
     */
    @Override
    @Transactional
    public ResponseEntity<ModuleResponse> authenticateOtp(String userId, String otp) {
        ValueCheckerUtil.isValidUUID(userId, () -> new BadRequestException(ErrorData.USER_ID_INVALID));
        OtpEntity otpEntity = otpEntityRepository.findByUserIdAndOtpForAndOtpStatus(userId, OtpFor.REGISTER, OtpStatus.ACTIVE)
                .orElseThrow(() -> new BadRequestException(ErrorData.USER_NOT_PRESENT_OTP));
        if (!otpEntity.getOtp().equals(otp)) {
            throw new BadRequestException(ErrorData.INVALID_OTP);
        }
        Date now = new Date();
        if (otpEntity.getExpiryDate().before(now))
            throw new BadRequestException(ErrorData.OTP_EXPIRED);
        UserAuth userEntity = userAuthRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorData.USER_NOT_FOUND_ID));
        if (Boolean.TRUE.equals(userEntity.getIsEnabled()))
            throw new BadRequestException(ErrorData.USER_ALREADY_ENABLED);
        userEntity.setIsEnabled(Boolean.TRUE);
        userEntity.setDateUpdated(now);
        UserAuth userAuth = userAuthRepository.save(userEntity);
        log.info("User Auth saved to Database : {}", userAuth);
        otpEntity.setDateUpdated(now);
        otpEntity.setOtpStatus(OtpStatus.DELETED);
        OtpEntity otpEntityResponse = otpEntityRepository.save(otpEntity);
        log.info("Otp Entity saved to database : {}", otpEntityResponse);
        applicationEventPublisher.publishEvent(new WelcomeMailEvent(userAuth));
        return ResponseEntity.accepted().body(ModuleResponse
                .builder()
                .message("User OTP has been authenticated successfully")
                .userId(userId)
                .build());
    }

    /**
     * Resends the registration OTP to the user identified by the provided userId.
     * If the user is already enabled, an exception is thrown.
     *
     * @param userId The ID of the user for whom to resend the OTP.
     * @return ResponseEntity containing the module response with success message and user ID.
     */
    @Override
    @Transactional
    public ResponseEntity<ModuleResponse> resendRegisterOtp(String userId) {
        ValueCheckerUtil.isValidUUID(userId, () -> new BadRequestException(ErrorData.USER_ID_INVALID));
        Date now = new Date();
        OtpEntity otpEntity = otpEntityRepository.findByUserIdAndOtpForAndOtpStatus(
                userId,
                OtpFor.REGISTER,
                OtpStatus.ACTIVE
        ).orElseThrow(() -> new BadRequestException(ErrorData.NO_OTP_PRESENT_FOR_RESEND));
        UserAuth userEntity = userAuthRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorData.USER_NOT_FOUND_ID));
        if (Boolean.TRUE.equals(userEntity.getIsEnabled())) {
            otpEntity.setDateUpdated(now);
            otpEntity.setOtpStatus(OtpStatus.DELETED);
            OtpEntity otpEntityResponse = otpEntityRepository.save(otpEntity);
            log.info("Otp Entity saved to database : {}", otpEntityResponse);
            throw new BadRequestException(ErrorData.USER_ALREADY_ENABLED);
        }
        otpEntity.setDateUpdated(now);
        otpEntity.setOtpStatus(OtpStatus.RESEND);
        OtpEntity otpEntityResponse = otpEntityRepository.save(otpEntity);
        log.info("Otp Entity saved to database : {}", otpEntityResponse);
        applicationEventPublisher.publishEvent(new UserRegistrationResendOtpEvent(userEntity, otpEntity));
        return ResponseEntity.ok(
                ModuleResponse
                        .builder()
                        .userId(userId)
                        .message("OTP sent successfully to your email")
                        .build());
    }

    /**
     * Sends a forgot password OTP to the user's email.
     * If the user is not found or not enabled, an exception is thrown.
     *
     * @param email The email of the user for whom to send the OTP.
     * @return ResponseEntity containing the module response with success message and user ID.
     */
    @Override
    public ResponseEntity<ModuleResponse> forgotPasswordOtp(String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorData.USER_NOT_FOUND_EMAIL));
        if (Boolean.FALSE.equals(userAuth.getIsEnabled()))
            throw new BadRequestException(ErrorData.USER_NOT_ENABLED);
        applicationEventPublisher.publishEvent(new ForgotPasswordOtpEvent(userAuth));
        return ResponseEntity.ok(
                ModuleResponse
                        .builder()
                        .userId(userAuth.getUserId())
                        .message("OTP Sent for Forgot Password")
                        .build()
        );
    }

    /**
     * Validates the forgot password request by checking the user ID and OTP.
     * If the OTP is valid and not expired, the user's password is updated.
     *
     * @param forgotPasswordRequest The request containing user ID, OTP, and new password.
     * @return ResponseEntity containing the module response with success message and user ID.
     */
    @Override
    public ResponseEntity<ModuleResponse> forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        ValueCheckerUtil.isValidUUID(forgotPasswordRequest.getUserId(), () -> new BadRequestException(ErrorData.USER_ID_INVALID));
        String userId = forgotPasswordRequest.getUserId();
        OtpEntity otpEntity = otpEntityRepository.findByUserIdAndOtpForAndOtpStatus(userId, OtpFor.FORGOT_PASSWORD, OtpStatus.ACTIVE)
                .orElseThrow(() -> new BadRequestException(ErrorData.USER_NOT_PRESENT_OTP));
        if (!otpEntity.getOtp().equals(forgotPasswordRequest.getOtp())) {
            throw new BadRequestException(ErrorData.INVALID_OTP);
        }
        Date now = new Date();
        if (otpEntity.getExpiryDate().before(now))
            throw new BadRequestException(ErrorData.OTP_EXPIRED);
        UserAuth userEntity = userAuthRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorData.USER_NOT_FOUND_ID));
        if (Boolean.FALSE.equals(userEntity.getIsEnabled()))
            throw new BadRequestException(ErrorData.USER_NOT_ENABLED);
        userEntity.setDateUpdated(now);
        userEntity.setPassword(passwordEncoder.encode(forgotPasswordRequest.getPassword()));
        userEntity.setLastPasswordChange(now);
        UserAuth userAuth = userAuthRepository.save(userEntity);
        log.info("User Auth saved to database : {}", userAuth);
        otpEntity.setDateUpdated(now);
        otpEntity.setOtpStatus(OtpStatus.DELETED);
        OtpEntity otpEntityResponse = otpEntityRepository.save(otpEntity);
        log.info("Otp Entity saved to database : {}", otpEntityResponse);
        return ResponseEntity.accepted().body(ModuleResponse
                .builder()
                .message("Password has been updated successfully. Please use new password next time you LogIn")
                .userId(userId.toString())
                .build());
    }

    /**
     * Resends the forgot password OTP to the user's email.
     * If the user is not found or not enabled, an exception is thrown.
     *
     * @param userId The ID of the user for whom to resend the OTP.
     * @return ResponseEntity containing the module response with success message and user ID.
     */
    @Override
    public ResponseEntity<ModuleResponse> resendForgotPasswordOtp(String userId) {
        ValueCheckerUtil.isValidUUID(userId, () -> new BadRequestException(ErrorData.USER_ID_INVALID));
        Date now = new Date();
        OtpEntity otpEntity = otpEntityRepository.findByUserIdAndOtpForAndOtpStatus(
                userId,
                OtpFor.FORGOT_PASSWORD,
                OtpStatus.ACTIVE
        ).orElseThrow(() -> new BadRequestException(ErrorData.NO_OTP_PRESENT_FOR_RESEND));
        UserAuth userAuth = userAuthRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorData.USER_NOT_FOUND_ID));
        otpEntity.setDateUpdated(now);
        otpEntity.setOtpStatus(OtpStatus.RESEND);
        OtpEntity otpEntityResponse = otpEntityRepository.save(otpEntity);
        log.info("Otp Entity saved to database : {}", otpEntityResponse);
        applicationEventPublisher.publishEvent(new ForgotPasswordResendOtpEvent(userAuth, otpEntity));
        return ResponseEntity.ok(
                ModuleResponse
                        .builder()
                        .userId(userId)
                        .message("OTP sent successfully to your email")
                        .build());
    }

    /**
     * Authenticates the forgot password OTP by checking if the provided user ID and OTP match an existing active OTP.
     * If the OTP is valid and not expired, a response is returned indicating that the OTP is valid.
     *
     * @param userId The ID of the user.
     * @param otp    The OTP to authenticate.
     * @return ResponseEntity containing the module response with success message and user ID.
     */
    @Override
    public ResponseEntity<ModuleResponse> authenticateForgotPasswordOtp(String userId, String otp) {
        ValueCheckerUtil.isValidUUID(userId, () -> new BadRequestException(ErrorData.USER_ID_INVALID));
        OtpEntity otpEntity = otpEntityRepository.findByUserIdAndOtpForAndOtpStatus(userId, OtpFor.FORGOT_PASSWORD, OtpStatus.ACTIVE)
                .orElseThrow(() -> new BadRequestException(ErrorData.USER_NOT_PRESENT_OTP));
        if (!otpEntity.getOtp().equals(otp)) {
            throw new BadRequestException(ErrorData.INVALID_OTP);
        }
        Date now = new Date();
        if (otpEntity.getExpiryDate().before(now))
            throw new BadRequestException(ErrorData.OTP_EXPIRED);
        return ResponseEntity.ok(ModuleResponse
                .builder()
                .message("Valid OTP")
                .userId(userId.toString())
                .build());
    }
}
