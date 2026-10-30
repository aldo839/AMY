package com.techdrawer.amy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException e) {

        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.NOT_FOUND.value());
        apiError.setMessage(e.getMessage());
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setError("Not Found");

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExistException(UserAlreadyExistException e) {

        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.CONFLICT.value());
        apiError.setMessage(e.getMessage());
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setError("Already Exists");

        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ExpiredCodeException.class)
    public ResponseEntity<ApiError> handleExpiredCodeException(ExpiredCodeException e) {

        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.UNAUTHORIZED.value());
        apiError.setMessage(e.getMessage());
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setError("Unauthorized");

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MissingArgumentException.class)
    public ResponseEntity<ApiError> handleMissingArgumentException(MissingArgumentException e) {

        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        apiError.setMessage(e.getMessage());
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setError("Bad Request");

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

}
