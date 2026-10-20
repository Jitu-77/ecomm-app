package com.ecommApp.ecomm_app.user.controllers;

import com.ecommApp.ecomm_app.user.dto.CustomerSignupRequest;
import com.ecommApp.ecomm_app.user.dto.CustomerSignupResponse;
import com.ecommApp.ecomm_app.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<CustomerSignupResponse>
        signUp(@RequestBody CustomerSignupRequest  request){
        CustomerSignupResponse reponse = userService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }
}
