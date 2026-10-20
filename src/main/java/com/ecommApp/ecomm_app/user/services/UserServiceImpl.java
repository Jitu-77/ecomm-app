package com.ecommApp.ecomm_app.user.services;


import com.ecommApp.ecomm_app.common.enums.UserRole;
import com.ecommApp.ecomm_app.common.exceptions.DuplicateResourceFoundException;
import com.ecommApp.ecomm_app.common.exceptions.ResourceNotFoundException;
import com.ecommApp.ecomm_app.user.dto.CustomerSignupRequest;
import com.ecommApp.ecomm_app.user.dto.CustomerSignupResponse;
import com.ecommApp.ecomm_app.user.entity.User;
import com.ecommApp.ecomm_app.user.mapper.UserMapper;
import com.ecommApp.ecomm_app.user.repository.UserRepository;
import jakarta.persistence.EntityListeners;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
@EntityListeners(AuditingEntityListener.class)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
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

}
