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
package com.revquix.sm.application.exception;

/*
  Developer: Rohit Parihar
  Project: ap-listing-service
  GitHub: github.com/rohit-zip
  File: AuthenticationException
 */

import org.springframework.http.HttpStatus;

/**
 * Exception class for handling authentication-related errors.
 * This class extends BaseException and provides constructors to set error data,
 * HTTP status, and optional field information.
 */
public class AuthenticationException extends BaseException {

    public AuthenticationException(ErrorData errorData) {
        this.setErrorData(errorData);
        this.setMessage(errorData.getMessage());
        this.setErrorCode(errorData.getCode());
        this.setCause(super.getCause());
        this.setLocalizedMessage(super.getLocalizedMessage());
        this.setHttpStatus(HttpStatus.UNAUTHORIZED);
    }

    public AuthenticationException(ErrorData errorData, HttpStatus httpStatus) {
        this.setErrorData(errorData);
        this.setMessage(errorData.getMessage());
        this.setErrorCode(errorData.getCode());
        this.setCause(super.getCause());
        this.setLocalizedMessage(super.getLocalizedMessage());
        this.setHttpStatus(httpStatus);
    }

    public AuthenticationException(ErrorData errorData, HttpStatus httpStatus, String field) {
        this.setErrorData(errorData);
        this.setMessage(errorData.getMessage());
        this.setErrorCode(errorData.getCode());
        this.setCause(super.getCause());
        this.setLocalizedMessage(super.getLocalizedMessage());
        this.setHttpStatus(httpStatus);
    }

    public AuthenticationException(ErrorData errorData, String message, String localizedMessage) {
        this.setErrorData(errorData);
        this.setMessage(message);
        this.setErrorCode(errorData.getCode());
        this.setCause(super.getCause());
        this.setLocalizedMessage(localizedMessage);
        this.setHttpStatus(HttpStatus.UNAUTHORIZED);
    }
}
