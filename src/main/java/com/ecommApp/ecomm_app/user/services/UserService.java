package com.ecommApp.ecomm_app.user.services;

import com.ecommApp.ecomm_app.user.dto.CustomerSignupRequest;
import com.ecommApp.ecomm_app.user.dto.CustomerSignupResponse;

public interface UserService {
    CustomerSignupResponse signUp(CustomerSignupRequest request);
}
