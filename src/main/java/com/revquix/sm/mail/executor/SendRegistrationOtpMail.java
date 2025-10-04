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
package com.revquix.sm.mail.executor;

/*
  Developer: Rohit Parihar
  Project: revquix-sm
  GitHub: github.com/rohit-zip
  File: SendRegistrationOtpMail
 */

import com.revquix.sm.auth.model.OtpEntity;
import com.revquix.sm.mail.payload.ZeptoMailResponse;
import com.revquix.sm.mail.service.ZeptoMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendRegistrationOtpMail {


    private final ZeptoMailService zeptoMailService;
    private final SpringTemplateEngine templateEngine;

    @Value("${otp.registration.mail-prefix}")
    private String prefix;

    @Value("${otp.registration.subject}")
    private String subject;

    public void execute(OtpEntity otpEntity) {
        log.info("SendRegistrationOtpMail::execute -> Sending registration OTP mail {}", otpEntity.toJson());
        ZeptoMailResponse zeptoMailResponse = zeptoMailService.sendEmail(
                String.format("%s@revquix.com", prefix),
                otpEntity.getEmail(),
                subject,
                prepareContext(otpEntity)
        );
        log.info("SendRegistrationOtpMail::execute -> OTP mail sent successfully with response: {}", zeptoMailResponse.toJson());
    }

    private String prepareContext(OtpEntity otpEntity) {
        log.info("SendRegistrationOtpMail::prepareContext -> Preparing email context for Registration OTP mail {}");
        Context context = new Context();
        context.setVariable("otpEntity", otpEntity);
        return templateEngine.process("RegistrationOtpMail", context);
    }


}
