package com.ecommApp.ecomm_app.user.repository;

import com.ecommApp.ecomm_app.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUserId(Long userId);

    Optional<Address> findByIdAndUserId(Long addressId, Long userId);

    @Modifying
    @Query("""
            UPDATE Address a
            SET a.isDefault = false
            WHERE a.user.id = :userId
            AND a.isDefault = true
            """)
    void clearDefaultAddress(
            @Param("userId") Long userId
    );
}