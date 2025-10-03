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
package com.revquix.sm.application.utils;

import com.revquix.sm.application.constants.ServiceConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: IpUtils.java
 */

/**
 * IpUtils provides utility methods for retrieving the client's IP address from an HttpServletRequest.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class IpUtils {

    private final HttpServletRequest httpServletRequest;

    /**
     * Retrieves the client's IP address from the HttpServletRequest.
     * It first checks the "X-Forwarded-For" header for the original client IP,
     * which is useful when the application is behind a proxy or load balancer.
     * If the header is not present, it falls back to the remote address of the request.
     *
     * @return The client's IP address as a String.
     */
    public String getRemoteAddress() {
        log.info("Fetching client IP address from request headers");
        String clientIp = httpServletRequest.getHeader(ServiceConstants.X_FORWARDED_FOR);
        if (clientIp != null && clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0].trim();
        }
        return Objects.isNull(clientIp) ? httpServletRequest.getRemoteAddr() : clientIp;
    }


}
