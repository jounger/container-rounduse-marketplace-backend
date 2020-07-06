package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Merchant;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

  Optional<Merchant> findByUsername(String username);
}
