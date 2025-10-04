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
package com.revquix.sm.mail.service;

/*
  Developer: Rohit Parihar
  Project: revquix-sm
  GitHub: github.com/rohit-zip
  File: ZeptoMailService
 */

import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.application.exception.InternalServerException;
import com.revquix.sm.mail.payload.ZeptoMailRequest;
import com.revquix.sm.mail.payload.ZeptoMailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZeptoMailService {

    private final WebClient webClient;

    @Value("${zeptomail.api.url}")
    private String zeptoMailApiUrl;

    @Value("${zeptomail.api.key}")
    private String zeptoMailApiKey;

    @Value("${zeptomail.api.timeout}")
    private int timeoutSeconds;

    @Retryable(
            retryFor = {
                    WebClientResponseException.class,
                    java.net.ConnectException.class,
                    java.util.concurrent.TimeoutException.class,
                    org.springframework.web.reactive.function.client.WebClientRequestException.class
            },
            noRetryFor = {
                    WebClientResponseException.BadRequest.class,
                    WebClientResponseException.Unauthorized.class,
                    WebClientResponseException.Forbidden.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 5000)
    )
    public ZeptoMailResponse sendEmail(String fromAddress, String toAddress, String subject, String htmlBody) {
        try {
            ZeptoMailRequest request = buildEmailRequest(fromAddress, toAddress, subject, htmlBody);

            ZeptoMailResponse response = webClient.post()
                    .uri(zeptoMailApiUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Zoho-enczapikey " + zeptoMailApiKey)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ZeptoMailResponse.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();

            log.info("Email sent successfully. Request ID: {}",
                    response != null ? response.getRequestId() : "N/A");

            return response;

        } catch (WebClientResponseException e) {
            log.warn("ZeptoMail API error - Status: {}, Body: {}, Attempt will be retried if applicable",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw e; // Let retry mechanism handle this
        } catch (Exception e) {
            log.error("Unexpected error sending email", e);
            throw e; // Let retry mechanism handle this
        }
    }

    private ZeptoMailRequest buildEmailRequest(String fromAddress, String toAddress, String subject, String htmlBody) {
        ZeptoMailRequest.From from = new ZeptoMailRequest.From(fromAddress);

        ZeptoMailRequest.EmailAddress emailAddress = new ZeptoMailRequest.EmailAddress(toAddress);
        ZeptoMailRequest.To to = new ZeptoMailRequest.To(emailAddress);

        return ZeptoMailRequest.builder()
                .from(from)
                .to(List.of(to))
                .subject(subject)
                .htmlBody(htmlBody)
                .build();
    }

    @Recover
    public ZeptoMailResponse recover(Exception e, String fromAddress, String toAddress, String subject, String htmlBody) {
        log.error("Failed to send email after all retry attempts. From: {}, To: {}, Subject: {}",
                fromAddress, toAddress, subject, e);
        throw new InternalServerException(ErrorData.FAILED_TO_SEND_MAIL_API_ERROR, e);
    }
}
