package com.techdrawer.amy.exception;

public class MissingArgumentException extends RuntimeException{
    public MissingArgumentException(String message){
        super(message);
    }
}
