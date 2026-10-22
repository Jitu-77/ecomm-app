package com.ecommApp.ecomm_app.user.services;

import com.ecommApp.ecomm_app.user.dto.AddressRequest;
import com.ecommApp.ecomm_app.user.dto.AddressResponse;

import java.util.List;

public interface AddressService {
    AddressResponse createAddress(
            Long userId,
            AddressRequest request
    );

    List<AddressResponse> getAllAddresses(
            Long userId
    );

    AddressResponse getAddressById(
            Long userId,
            Long addressId
    );

    AddressResponse updateAddress(
            Long userId,
            Long addressId,
            AddressRequest request
    );

    void deleteAddress(
            Long userId,
            Long addressId
    );

    AddressResponse toggleDefaultAddress(
            Long userId,
            Long addressId
    );
}
