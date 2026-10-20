package com.ecommApp.ecomm_app.user.services;

import com.ecommApp.ecomm_app.user.dto.CustomerSignupRequest;
import com.ecommApp.ecomm_app.user.dto.CustomerSignupResponse;
import com.ecommApp.ecomm_app.user.entity.User;

public interface UserService {
    User getUserById(Long id);
//    CustomerSignupResponse signUp(CustomerSignupRequest request);
}
