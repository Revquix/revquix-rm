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
package com.revquix.sm.mail.processor;

/*
  Developer: Rohit Parihar
  Project: revquix-sm
  GitHub: github.com/rohit-zip
  File: SendRegistrationOtpMail
 */

import com.revquix.sm.application.utils.IpUtils;
import com.revquix.sm.auth.enums.OtpFor;
import com.revquix.sm.auth.enums.OtpStatus;
import com.revquix.sm.auth.model.OtpEntity;
import com.revquix.sm.auth.model.UserAuth;
import com.revquix.sm.auth.repository.OtpEntityRepository;
import com.revquix.sm.auth.utils.OtpGenerator;
import com.revquix.sm.mail.executor.SendRegistrationOtpMail;
import com.revquix.sm.mail.service.ZeptoMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendRegistrationOtpMailProcessor {

    private final OtpEntityRepository otpEntityRepository;
    private final SendRegistrationOtpMail sendRegistrationOtpMail;

    @Value("${otp.registration.time-expiration:7}")
    private int timeExpiration;

    @Value("${otp.registration.log-enabled:false}")
    private boolean logEnabled;

    public void process(UserAuth userAuth) {
        log.info("SendRegistrationOtpMail::process -> Sending registration OTP mail for userAuth: {}", userAuth.toJson());
        Optional<OtpEntity> byEmailAndOtpForAndOtpStatus = otpEntityRepository.findByEmailAndOtpForAndOtpStatus(
                userAuth.getEmail(),
                OtpFor.REGISTER,
                OtpStatus.ACTIVE
        );
        if (byEmailAndOtpForAndOtpStatus.isPresent()) {
            Date now = new Date();
            log.info("SendRegistrationOtpMail::process -> Active OTP already exists for email: {}", userAuth.getEmail());
            OtpEntity otpEntity = byEmailAndOtpForAndOtpStatus.get();
            if (otpEntity.getExpiryDate().after(now)) {
                log.info("SendRegistrationOtpMail::process -> Reusing existing OTP for email: {}", userAuth.getEmail());
                otpEntity.setUserId(userAuth.getUserId());
                otpEntity.setOtpStatus(OtpStatus.ACTIVE);
                otpEntity.setDateUpdated(now);
                if (logEnabled) log.info("SendRegistrationOtpMail::process -> Existing OTP details email:{}, otp:{}", otpEntity.getEmail(), otpEntity.getOtp());
                otpEntityRepository.save(otpEntity);
                log.info("SendRegistrationOtpMail::process -> OtpEntity saved successfully for email: {}", userAuth.getEmail());
                return;
            } else {
                log.info("SendRegistrationOtpMail::process -> Existing OTP expired, generating new OTP for email: {}", userAuth.getEmail());
                otpEntity.setOtpStatus(OtpStatus.EXPIRED);
                otpEntity.setDateUpdated(now);
                otpEntityRepository.save(otpEntity);
                log.info("SendRegistrationOtpMail::process -> Existing OtpEntity marked as EXPIRED for email: {}", userAuth.getEmail());
            }
        }
        log.info("SendRegistrationOtpMail::process -> Generating new OTP for email: {}", userAuth.getEmail());
        if (Boolean.TRUE.equals(userAuth.getIsEnabled())) {
            log.warn("SendRegistrationOtpMail::process -> User is already enabled, skipping OTP generation for userId: {}", userAuth.getUserId());
            return;
        }
        OtpEntity newOtpEntity = buildOtpEntity(userAuth);
        OtpEntity savedOtpEntity = otpEntityRepository.save(newOtpEntity);
        log.info("SendRegistrationOtpMail::process -> New OtpEntity saved successfully for email: {}", userAuth.getEmail());
        sendRegistrationOtpMail.execute(savedOtpEntity);
        if (logEnabled) log.info("SendRegistrationOtpMail::process -> New OTP details email:{}, otp:{}", savedOtpEntity.getEmail(), savedOtpEntity.getOtp());
    }

    private OtpEntity buildOtpEntity(UserAuth userAuth) {
        Date now = new Date();
        String otp = OtpGenerator.generateOtp.get();
        OtpEntity otpEntity = OtpEntity
                .builder()
                .otp(otp)
                .userId(userAuth.getUserId())
                .email(userAuth.getEmail())
                .otpFor(OtpFor.REGISTER)
                .dateGenerated(now)
                .dateUpdated(now)
                .expiryDate(new Date(System.currentTimeMillis() + (1000 * 60 * timeExpiration)))
                .timesSent(0)
                .otpStatus(OtpStatus.ACTIVE)
                .build();
        log.info("SendRegistrationOtpMail::buildOtpEntity -> Built new OtpEntity for email: {}, otpEntity:{}", userAuth.getEmail(), otpEntity.toJson());
        return otpEntity;
    }
}
