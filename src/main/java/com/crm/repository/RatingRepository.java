package com.crm.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

  @Query(value = "FROM Rating r WHERE r.sender.username = :username AND r.createdAt > :rewind")
  Page<Rating> findBySender(@Param("username") String username, @Param("rewind") Date rewind, Pageable pageable);

  @Query(value = "FROM Rating r WHERE r.receiver.username = :username AND r.createdAt > :rewind")
  Page<Rating> findByReceiver(@Param("username") String username, @Param("rewind") Date rewind, Pageable pageable);

  @Query(value = "FROM Rating r WHERE (r.receiver.username = :username OR r.sender.id = :id) AND r.createdAt > :rewind")
  Page<Rating> findByUser(@Param("username") String username, @Param("rewind") Date rewind, Pageable pageable);

  @Query(value = "SELECT AVG(r.ratingValue) FROM Rating r WHERE r.receiver.id = :id AND r.createdAt > :rewind")
  Double calcAvgRatingValueByReceiver(@Param("id") Long id, @Param("rewind") Date rewind);

  @Query(value = "FROM Rating r WHERE (r.receiver.username = :username OR r.sender.username = :username) "
      + "AND r.contract.id = :id AND r.createdAt > :rewind")
  Page<Rating> findByContract(@Param("id") Long id, @Param("username") String username, @Param("rewind") Date rewind, Pageable pageable);

  @Query(value = "SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END "
      + "FROM Rating r LEFT JOIN r.contract c LEFT JOIN c.combined cb "
      + "LEFT JOIN cb.bid b LEFT JOIN b.biddingDocument bd "
      + "WHERE c.id = :id AND r.sender.username = :username")
  Boolean existsByUserAndContract(@Param("id") Long contractId, @Param("username") String username);
}
