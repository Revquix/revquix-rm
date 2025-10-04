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

import com.revquix.sm.application.constants.ServiceConstants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: InternalServerException.java
 */

/**
 * InternalException is a custom exception class that extends BaseException.
 * It represents internal server errors and sets the HTTP status to 500 (INTERNAL_SERVER_ERROR).
 */
@Getter
@Setter
public class InternalServerException extends BaseException {

    public InternalServerException(ErrorData errorData) {
        this.setErrorData(errorData);
        this.setMessage(errorData.getMessage());
        this.setErrorCode(errorData.getCode());
        this.setCause(super.getCause());
        this.setLocalizedMessage(super.getLocalizedMessage());
        this.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalServerException(ErrorData errorData, Throwable cause) {
        this.setErrorData(errorData);
        this.setMessage(errorData.getMessage());
        this.setErrorCode(errorData.getCode());
        this.setCause(cause);
        this.setLocalizedMessage(cause.getLocalizedMessage());
        this.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
