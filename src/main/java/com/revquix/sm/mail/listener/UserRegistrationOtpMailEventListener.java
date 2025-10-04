package com.revquix.sm.mail.listener;

/*
  Developer: Rohit Parihar
  Project: revquix-sm
  GitHub: github.com/rohit-zip
  File: UserRegistrationOtpMailEventListener
 */

import com.revquix.sm.application.constants.ServiceConstants;
import com.revquix.sm.auth.events.UserRegistrationOtpMailEvent;
import com.revquix.sm.mail.processor.SendRegistrationOtpMail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationOtpMailEventListener {

    private final SendRegistrationOtpMail sendRegistrationOtpMail;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventTaskExecutor")
    public void onEvent(UserRegistrationOtpMailEvent userRegistrationOtpEvent) {
        if (Objects.isNull(userRegistrationOtpEvent)) {
            log.warn("UserRegistrationOtpMailEventListener::onEvent -> Received null event, skipping processing.");
            return;
        }
        String breadcrumbId = userRegistrationOtpEvent.getBreadcrumbId();
        try {
            log.info("UserRegistrationOtpMailEventListener::onEvent -> UserRegistrationOtpMailEvent : {}, breadcrumbId: {}", userRegistrationOtpEvent.getUserAuth(), userRegistrationOtpEvent.getBreadcrumbId());
            MDC.put(ServiceConstants.BREADCRUMB_ID, userRegistrationOtpEvent.getBreadcrumbId());
            sendRegistrationOtpMail.process(userRegistrationOtpEvent.getUserAuth());
        } finally {
            MDC.clear();
        }
    }
}
