package com.crm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Container;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {

  @Query(value = "SELECT c FROM Container c WHERE c.forwarder.id = :id")
  Page<Container> findByForwarderId(@Param("id") Long id, Pageable pageable);

  //find by consignment
  @Query(value = "FROM Container c WHERE c.shippingLine.id = :shippingLineId"
      + " AND containerType.id = :containerTypeId AND c.status != :status"
      + " AND c.emptyTime < :packingTime AND c.freeTime < :cutOfTime"
      + " AND c.portOfDelivery.id = :portOfLoadingId")
  List<Container> findByConsignment(@Param("shippingLineId") Long shippingLineId
      , @Param("containerTypeId") Long containerTypeId, @Param("status") int status
      , @Param("packingTime") LocalDateTime packingTime, @Param("cutOfTime") LocalDateTime cutOfTime
      , @Param("portOfLoadingId") Long portOfLoadingId);
}
