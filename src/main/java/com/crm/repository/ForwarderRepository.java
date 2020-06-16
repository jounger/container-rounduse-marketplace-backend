package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Forwarder;

@Repository
public interface ForwarderRepository extends JpaRepository<Forwarder, Long>{

	Optional<Forwarder> findByUsername(String username);
}
