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
package com.revquix.sm.application.config;

import com.revquix.sm.application.constants.ServiceConstants;
import com.revquix.sm.application.utils.IpUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: BloggiosApplicationMDCFilter.java
 */

/**
 * ApplicationMDCFilter is a filter that adds a unique breadcrumb ID, remote address,
 * and response status to the MDC (Mapped Diagnostic Context) for each request.
 * This information can be used for logging and tracing requests throughout the application.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class ApplicationMDCFilter extends OncePerRequestFilter {

    private final IpUtils ipUtils;

    /**
     * Adds a unique breadcrumb ID, remote address, and response status to the MDC for each request.
     * If the request does not contain a breadcrumb ID, a new one is generated.
     * The MDC is cleared after the request is processed to prevent memory leaks.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if an error occurs during filtering
     * @throws IOException      if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("{} >> doFilterInternal", getClass().getSimpleName());
        String requestId = request.getHeader(ServiceConstants.BREADCRUMB_ID);
        if (Objects.isNull(requestId) || requestId.isEmpty()) {
            log.debug("{} >> Breadcrumb Id is not present in the request. Auto Generating the breadcrumbId", getClass().getSimpleName());
            requestId = UUID.randomUUID().toString();
        }
        MDC.put(ServiceConstants.BREADCRUMB_ID, requestId);
        MDC.put(ServiceConstants.REMOTE_ADDRESS, ipUtils.getRemoteAddress());
        MDC.put(ServiceConstants.RESPONSE_STATUS, "Pending");

        try {
            filterChain.doFilter(request, response);
            response.getStatus();
            MDC.put(ServiceConstants.RESPONSE_STATUS, String.valueOf(response.getStatus()));
        } finally {
            log.debug("{} >> doFilterInternal -> Removing MDC entry data", getClass().getSimpleName());
            MDC.clear();
        }
    }
}
