package com.ecommApp.ecomm_app.common.exceptions;

public class ProductQuantityException extends IllegalArgumentException {
    public ProductQuantityException(String message){
        super(message);
    }
}
