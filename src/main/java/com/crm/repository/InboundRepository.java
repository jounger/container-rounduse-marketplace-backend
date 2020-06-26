package com.crm.repository;

<<<<<<< HEAD
import org.springframework.data.jpa.repository.JpaRepository;
=======
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
>>>>>>> master
import org.springframework.stereotype.Repository;

import com.crm.models.Inbound;

@Repository
<<<<<<< HEAD
public interface InboundRepository extends JpaRepository<Inbound, Long>{

=======
public interface InboundRepository extends JpaRepository<Inbound, Long> {

  @Query(value = "SELECT i FROM Inbound i WHERE i.forwarder.id = :id")
  Page<Inbound> getInboundsByFowarder(@Param("id") Long id, Pageable pageable);
>>>>>>> master
}
