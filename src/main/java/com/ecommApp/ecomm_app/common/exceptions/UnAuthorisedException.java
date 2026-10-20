package com.ecommApp.ecomm_app.common.exceptions;

    public class UnAuthorisedException extends RuntimeException{
    public UnAuthorisedException(String message) {
        super(message);
    }
}