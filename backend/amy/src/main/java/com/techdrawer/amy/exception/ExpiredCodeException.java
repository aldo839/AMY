package com.techdrawer.amy.exception;

public class ExpiredCodeException extends RuntimeException {
    public ExpiredCodeException(String message) {
        super(message);
    }
}
