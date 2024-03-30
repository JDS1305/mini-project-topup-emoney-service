package com.funprojectbylely.emoneytopupservice.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InsuficientException extends RuntimeException {
    public InsuficientException(String message) {
        super(message);
    }
}
