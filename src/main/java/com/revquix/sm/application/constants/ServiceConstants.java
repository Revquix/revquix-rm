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
package com.revquix.sm.application.constants;

/*
  Developer: Rohit Parihar
  Project: ap-auth-provider
  GitHub: github.com/rohit-zip
  File: ServiceConstants
 */

import lombok.experimental.UtilityClass;

/**
 * ServiceConstants is a utility class that defines constant values used
 * throughout the service layer of the application, promoting consistency
 * and reducing the likelihood of errors due to hard-coded strings.
 */
@UtilityClass
public class ServiceConstants {

    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String PKCS12 = "PKCS12";
    public static final String INTERNAL_ERROR_CODE = "INTERNAL-EXCEPTION";
    public static final String BREADCRUMB_ID = "breadcrumbId";
    public static final String REMOTE_ADDRESS = "remoteAddress";
    public static final String RESPONSE_STATUS = "responseStatus";
    public static final String REVQUIX = "Revquix";
    public static final Object ACCESS_TOKEN_TYPE = "access";
    public static final String REFRESH_TOKEN_TYPE = "refresh";
    public static final String AUTHORITIES = "authorities";
    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String CLIENT_ID = "clientId";
    public static final String ROLES = "roles";
    public static final String SCOPES = "scopes";
    public static final String TOKEN_TYPE = "type";
    public static final Object UNIVERSAL_ORIGIN = "0.0.0.0";
    public static final String ORIGINS = "origins";
    public static final String JTI = "jti";
    public static final String AUTHENTICATION_TYPE = "authenticationType";
    public static final String CLIENT_NAME = "clientName";
    public static final String CLIENT_TYPE = "clientType";
    public static final String ORIGIN = "Origin";
    public static final String SUPER_DOMAIN = "revquix.com";
    public static final String PROCESSING_TIME = "Processing Time ({}) -> {}ms";
    public static final String CLIENT_SECRET_PREFIX_STRING = "auth.revquix.com";
    public static final String ACCESS_DENIED_ERROR_CODE = "ACCESS-DENIED";
}
