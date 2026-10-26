package com.ecommApp.ecomm_app.carts.services;

import com.ecommApp.ecomm_app.carts.dto.CheckoutRequest;
import com.ecommApp.ecomm_app.carts.dto.CheckoutResponse;

public interface CheckoutService {
    CheckoutResponse checkout(CheckoutRequest request);
}
