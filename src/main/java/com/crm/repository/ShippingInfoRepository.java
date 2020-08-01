package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.ShippingInfo;

@Repository
public interface ShippingInfoRepository extends JpaRepository<ShippingInfo, Long> {

  @Query(value = "FROM ShippingInfo si LEFT JOIN si.container c LEFT JOIN c.driver d WHERE d.username = :username")
  Page<ShippingInfo> findByDriver(@Param("username") String username, Pageable pageable);

  @Query(value = "FROM ShippingInfo si WHERE si.outbound.id = :id")
  Page<ShippingInfo> findByOutbound(@Param("id") Long outboundId, Pageable pageable);

  @Query(value = "SELECT CASE WHEN COUNT(si) = 0 THEN TRUE ELSE FALSE END"
      + " FROM ShippingInfo si LEFT JOIN si.container c LEFT JOIN c.driver d"
      + " LEFT JOIN d.forwarder f WHERE si.id = :id AND f.username = :username")
  Boolean isForwarder(@Param("id") Long id, @Param("username") String username);
}