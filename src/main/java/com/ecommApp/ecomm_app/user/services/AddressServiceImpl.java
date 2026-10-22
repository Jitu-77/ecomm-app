package com.ecommApp.ecomm_app.user.services;

import com.ecommApp.ecomm_app.common.exceptions.ResourceNotFoundException;
import com.ecommApp.ecomm_app.user.dto.AddressRequest;
import com.ecommApp.ecomm_app.user.dto.AddressResponse;
import com.ecommApp.ecomm_app.user.entity.Address;
import com.ecommApp.ecomm_app.user.entity.User;
import com.ecommApp.ecomm_app.user.mapper.AddressMapper;
import com.ecommApp.ecomm_app.user.repository.AddressRepository;
import com.ecommApp.ecomm_app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressServiceImpl implements  AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    public AddressResponse createAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "User not found with id: " + userId   ) );
        Address address = addressMapper.toEntity(request);
        address.setUser(user);
        // null should be treated as false
        boolean isDefault = Boolean.TRUE.equals(request.isDefault());
        if (isDefault) {
            // Remove default flag from all existing addresses
            addressRepository.clearDefaultAddress(userId);
        }
        address.setIsDefault(isDefault);
        LocalDateTime now = LocalDateTime.now();
        address.setCreatedAt(now);
        address.setUpdatedAt(now);

        if (Boolean.TRUE.equals(request.isDefault())) {
            // We'll handle existing default addresses here.
        }
        Address savedAddress = addressRepository.save(address);
        return addressMapper.toResponse(savedAddress);
    }

    @Override
    public List<AddressResponse> getAllAddresses(Long userId) {
        List<Address> addresses = addressRepository.findAllByUserId(userId);
        return addresses.stream()
                .map(address -> addressMapper.toResponse(address))
                .toList();
    }

    @Override
    public AddressResponse getAddressById(Long userId, Long addressId) {
        Address address = addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with id: " + addressId
                        )
                );

        return addressMapper.toResponse(address);
    }

    @Override
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with id: " + addressId
                        )
                );
        // null should be treated as false
        boolean isDefault = Boolean.TRUE.equals(request.isDefault());
        if (isDefault) {
            // Remove default flag from all existing addresses
            addressRepository.clearDefaultAddress(userId);
        }
        address.setIsDefault(isDefault);
        address.setStreet(request.street());
        address.setCity(request.city());
        address.setState(request.state());
        address.setCountry(request.country());
        address.setZipcode(request.zipcode());
        address.setAddressType(request.addressType());
        address.setUpdatedAt(LocalDateTime.now());

        Address updatedAddress =
                addressRepository.save(address);

        return addressMapper.toResponse(updatedAddress);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with id: " + addressId
                        )
                );

        addressRepository.delete(address);
    }

    @Override
    public AddressResponse toggleDefaultAddress(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with id: " + addressId));
        boolean currentDefault = Boolean.TRUE.equals(address.getIsDefault());
        if (currentDefault) {
            // Current default -> remove default status
            address.setIsDefault(false);
        } else {
            // Current non-default -> make it default
            // First remove default from all other addresses
            addressRepository.clearDefaultAddress(userId);
            address.setIsDefault(true);
        }
        address.setUpdatedAt(LocalDateTime.now());
        Address updatedAddress = addressRepository.save(address);
        return addressMapper.toResponse(updatedAddress);
    }
}
