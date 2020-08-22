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

  @Query(value = "SELECT si FROM ShippingInfo si LEFT JOIN si.container c LEFT JOIN c.driver d WHERE d.username = :username")
  Page<ShippingInfo> findByDriver(@Param("username") String username, Pageable pageable);
  
  @Query(value = "SELECT si FROM ShippingInfo si LEFT JOIN si.container c LEFT JOIN c.driver d WHERE d.username = :username"
      + " AND si.status = :status")
  Page<ShippingInfo> findByDriver(@Param("username") String username, @Param("status") String status, Pageable pageable);

  @Query(value = "SELECT si FROM ShippingInfo si WHERE si.outbound.id = :id")
  Page<ShippingInfo> findByOutbound(@Param("id") Long outboundId, Pageable pageable);

  @Query(value = "SELECT si FROM ShippingInfo si WHERE si.contract.combined.id = :id")
  Page<ShippingInfo> findByCombined(@Param("id") Long combinedId, Pageable pageable);

  @Query(value = "SELECT CASE WHEN COUNT(si) > 0 THEN TRUE ELSE FALSE END"
      + " FROM ShippingInfo si LEFT JOIN si.container c LEFT JOIN c.driver d"
      + " LEFT JOIN d.forwarder f WHERE si.id = :id AND f.username = :username")
  Boolean isForwarder(@Param("id") Long id, @Param("username") String username);
  
  @Query(value = "SELECT CASE WHEN COUNT(si) = b.unit THEN TRUE ELSE FALSE END"
      + " FROM ShippingInfo si LEFT JOIN si.contract ct LEFT JOIN ct.combined c LEFT JOIN c.bid b"
      + " LEFT JOIN b.biddingDocument bd LEFT JOIN bd.outbound ob LEFT JOIN ob.booking b"
      + " WHERE ob.id = :id")
  Boolean isAllDeliveredByOutbound(@Param("id") Long outboundId);
}
