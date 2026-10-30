package com.techdrawer.amy.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {

    private String message;
    private int status;
    private String error;
    private LocalDateTime timestamp;

}
