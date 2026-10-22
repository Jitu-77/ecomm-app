package com.ecommApp.ecomm_app.user.controllers;


import com.ecommApp.ecomm_app.user.auth.SecurityUtils;
import com.ecommApp.ecomm_app.user.dto.AddressRequest;
import com.ecommApp.ecomm_app.user.dto.AddressResponse;
import com.ecommApp.ecomm_app.user.entity.User;
import com.ecommApp.ecomm_app.user.services.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@PreAuthorize("hasAnyRole('CUSTOMER', 'MERCHANT')")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(
            @Valid @RequestBody AddressRequest request
    ) {
        User currentUser = SecurityUtils.getCurrentUser();
        AddressResponse response =
                addressService.createAddress(
                        currentUser.getId(),
                        request
                );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddresses() {
        User currentUser = SecurityUtils.getCurrentUser();
        List<AddressResponse> response =
                addressService.getAllAddresses(
                        currentUser.getId()
                );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> getAddressById(
            @PathVariable Long addressId
    ) {
        User currentUser = SecurityUtils.getCurrentUser();
        AddressResponse response =
                addressService.getAddressById(
                        currentUser.getId(),
                        addressId
                );
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request
    ) {
        User currentUser = SecurityUtils.getCurrentUser();
        AddressResponse response =
                addressService.updateAddress(
                        currentUser.getId(),
                        addressId,
                        request
                );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId
    ) {
        User currentUser = SecurityUtils.getCurrentUser();
        addressService.deleteAddress(
                currentUser.getId(),
                addressId
        );
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<AddressResponse> toggleDefaultAddress(
            @PathVariable Long addressId
    ) {
        User currentUser = SecurityUtils.getCurrentUser();
        AddressResponse response =
                addressService.toggleDefaultAddress(
                        currentUser.getId(),
                        addressId
                );
        return ResponseEntity.ok(response);
    }
}
