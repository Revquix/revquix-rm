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

import com.revquix.sm.application.exception.InternalServerException;
import com.revquix.sm.mail.payload.ZeptoMailRequest;
import com.revquix.sm.mail.payload.ZeptoMailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    public ZeptoMailResponse sendEmail(String fromAddress, String toAddress, String toName, String subject, String htmlBody) {
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
            log.error("ZeptoMail API error - Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new InternalServerException(

            )
        } catch (Exception e) {
            log.error("Failed to send email via ZeptoMail: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
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
}
