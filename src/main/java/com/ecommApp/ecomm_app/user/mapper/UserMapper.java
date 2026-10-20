package com.ecommApp.ecomm_app.user.mapper;


import com.ecommApp.ecomm_app.user.dto.CustomerSignupRequest;
import com.ecommApp.ecomm_app.user.dto.CustomerSignupResponse;
import com.ecommApp.ecomm_app.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel =  "spring")
public interface UserMapper {
    User toEntity(CustomerSignupRequest request);

    CustomerSignupResponse toResponse(User user);
}
