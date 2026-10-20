package com.ecommApp.ecomm_app.user.auth;

import com.ecommApp.ecomm_app.common.exceptions.DuplicateResourceFoundException;
import com.ecommApp.ecomm_app.common.exceptions.ResourceNotFoundException;
import com.ecommApp.ecomm_app.user.dto.*;
import com.ecommApp.ecomm_app.user.entity.User;
import com.ecommApp.ecomm_app.user.mapper.UserMapper;
import com.ecommApp.ecomm_app.user.repository.UserRepository;
import com.ecommApp.ecomm_app.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserMapper userMapper;

    public CustomerSignupResponse signUp(CustomerSignupRequest request) {

        if (userRepository.existsByEmail(request.email())) {
//            throw new RuntimeException("Email already registered");
            throw new DuplicateResourceFoundException("Email already registered");
        }

        if(request.role()== null || request.role().toString() ==""){
            throw new ResourceNotFoundException("User must have roles");
        }


//        User user = new User();
//
//        user.setEmail(request.email());
//        user.setPassword(request.password());
//        user.setFirstName(request.firstName());
//        user.setLastName(request.lastName());
//        user.setMobile(request.mobile());
//        user.setRole(UserRole.CUSTOMER);
//        user.setActive(true);
//
//        User savedUser = userRepository.save(user);

        User user = userMapper.toEntity(request);
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(request.password()));
        User savedUser = userRepository.save(user);
//        return new CustomerSignupResponse(
//                savedUser.getId(),
//                savedUser.getEmail(),
//                savedUser.getFirstName(),
//                savedUser.getLastName(),
//                savedUser.getMobile(),
//                savedUser.getRole()
//        );

        return userMapper.toResponse(savedUser);
    }


    public LoginResponse login(LoginRequest loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.email(), loginDto.password()
        ));

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new LoginResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }


    public RefreshTokenResponse refreshToken(String refreshToken) {
        Long id = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+id));
        String refreshTokenResponse =  jwtService.generateAccessToken(user);
        return new RefreshTokenResponse(
                refreshTokenResponse
        );
    }
}
