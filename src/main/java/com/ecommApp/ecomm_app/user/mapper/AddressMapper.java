package com.ecommApp.ecomm_app.user.mapper;

import com.ecommApp.ecomm_app.user.dto.AddressRequest;

import com.ecommApp.ecomm_app.user.dto.AddressResponse;
import com.ecommApp.ecomm_app.user.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper {
    Address toEntity(AddressRequest request);

    AddressResponse toResponse(Address address);
}
