package com.ecommApp.ecomm_app.user.controllers;

import com.ecommApp.ecomm_app.user.auth.AuthService;
import com.ecommApp.ecomm_app.user.dto.*;
import com.ecommApp.ecomm_app.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationServiceException;
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<CustomerSignupResponse>
        signUp(@RequestBody CustomerSignupRequest  request){
        CustomerSignupResponse reponse = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse>
    signUp(@RequestBody LoginRequest request){
        LoginResponse reponse = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(reponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        String refreshToken =request.refreshToken();
        if(refreshToken == null || refreshToken.isBlank()){
           throw  new AuthenticationServiceException("Refresh token not found inside the Cookies");
        }

        RefreshTokenResponse accessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(accessToken);
    }
}
