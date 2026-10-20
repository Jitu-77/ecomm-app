package com.ecommApp.ecomm_app.user.repository;

import com.ecommApp.ecomm_app.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}