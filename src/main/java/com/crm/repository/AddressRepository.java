package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>{

}
