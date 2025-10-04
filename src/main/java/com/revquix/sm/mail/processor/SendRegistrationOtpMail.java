package com.revquix.sm.mail.processor;

/*
  Developer: Rohit Parihar
  Project: revquix-sm
  GitHub: github.com/rohit-zip
  File: SendRegistrationOtpMail
 */

import com.revquix.sm.auth.model.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendRegistrationOtpMail {

    public void process(UserAuth userAuth) {
        log.info("SendRegistrationOtpMail::process -> Sending registration OTP mail", getClass().getSimpleName());

    }
}
