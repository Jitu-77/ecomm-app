package com.ecommApp.ecomm_app.common.exceptions;

public class DuplicateResourceFoundException extends RuntimeException{
    public DuplicateResourceFoundException(String message){
        super(message);
    }
}