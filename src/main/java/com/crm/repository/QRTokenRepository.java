package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.QRToken;

@Repository
public interface QRTokenRepository extends JpaRepository<QRToken, Long> {

  Optional<QRToken> findByToken(String token);

}
