package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crm.models.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

  Boolean existsByToken(String token);

  Optional<PasswordResetToken> findByToken(String token);

}
