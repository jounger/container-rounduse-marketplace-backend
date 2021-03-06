package com.crm.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.enums.EnumUserStatus;
import com.crm.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  @Query(value = "SELECT u FROM User u LEFT JOIN u.roles r WHERE r.name = :name")
  List<User> findByRole(@Param("name") String name);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  Boolean existsByPhone(String phone);

  Page<User> findByStatus(EnumUserStatus status, Pageable pageable);

  @Query(value = "SELECT COUNT(u) FROM User u WHERE u.createdAt > :startDate AND u.createdAt < :endDate")
  Integer countUserByOperator(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(u) FROM User u WHERE u.createdAt > :startDate AND u.createdAt < :endDate AND u.status IN :statusList")
  Integer countUserByOperator(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
      @Param("statusList") List<String> statusList);

}
