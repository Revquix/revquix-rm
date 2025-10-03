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

import com.revquix.sm.application.payload.ExceptionResponse;
import com.revquix.sm.application.utils.ErrorResponseGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: GlobalExceptionHandler.java
 */

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    /**
     * Handles BadRequestException and returns a structured error response.
     *
     * @param exception the BadRequestException that was thrown
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> badRequestException(BadRequestException exception) {
        log.error("BadRequestException Occurred >> {}", exception.toString());
        ExceptionResponse exceptionResponse = ErrorResponseGeneratorUtil.generate(exception);
        return new ResponseEntity<>(
                exceptionResponse,
                exception.getHttpStatus()
        );
    }

    /**
     * Handles Authentication Exception and returns a structured error response.
     *
     * @param exception the BadRequestException that was thrown
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> badRequestException(AuthenticationException exception) {
        log.error("AuthenticationException Occurred >> {}", exception.toString());
        ExceptionResponse exceptionResponse = ErrorResponseGeneratorUtil.generate(exception);
        return new ResponseEntity<>(
                exceptionResponse,
                exception.getHttpStatus()
        );
    }

    /**
     * Handles InternalException and returns a structured error response.
     *
     * @param exception the InternalException that was thrown
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ExceptionResponse> internalException(InternalServerException exception) {
        log.error("InternalException Occurred >> {}", exception.toString());
        ExceptionResponse exceptionResponse = ErrorResponseGeneratorUtil.generate(exception);
        return new ResponseEntity<>(
                exceptionResponse,
                exception.getHttpStatus()
        );
    }

    /**
     * Handles any other exceptions that are not specifically handled.
     *
     * @param exception the Exception that was thrown
     * @return ResponseEntity containing a generic error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> exception(Exception exception) {
        log.error("Exception Occurred >> {}", exception.toString());
        ExceptionResponse exceptionResponse = ExceptionResponse
                .builder()
                .message(ErrorData.INTERNAL_ERROR.getMessage())
                .code(ErrorData.INTERNAL_ERROR.getCode())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .localizedMessage(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    /**
     * Handles AuthorizationDeniedException and returns a structured error response.
     *
     * @param exception the AuthorizationDeniedException that was thrown
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ExceptionResponse> authorizationDeniedException(AuthorizationDeniedException exception) {
        log.error("AuthorizationDeniedException Occurred >> {}", exception.toString());
        ExceptionResponse exceptionResponse = ErrorResponseGeneratorUtil.generate(exception);
        return new ResponseEntity<>(
                exceptionResponse,
                HttpStatus.FORBIDDEN
        );
    }
}
