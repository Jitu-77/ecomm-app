package com.ecommApp.ecomm_app.common.exceptions;

public class InsufficientQuantityException extends RuntimeException{
    public InsufficientQuantityException(String message){
        super(message);
    }
}


