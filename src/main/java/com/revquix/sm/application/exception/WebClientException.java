package com.revquix.sm.application.exception;

/*
  Developer: Rohit Parihar
  Project: revquix-sm
  GitHub: github.com/rohit-zip
  File: WebClientException
 */

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class WebClientException extends BaseException {

    public WebClientException(ErrorData errorData) {
        this.setErrorData(errorData);
        this.setMessage(errorData.getMessage());
        this.setErrorCode(errorData.getCode());
        this.setCause(super.getCause());
        this.setLocalizedMessage(super.getLocalizedMessage());
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
    }
}
