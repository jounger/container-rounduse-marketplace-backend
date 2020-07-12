package com.crm.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

  @Query(value = "FROM Rating r WHERE r.sender.id = :id")
  Page<Rating> findBySender(@Param("id") Long id, Pageable pageable);

  @Query(value = "FROM Rating r WHERE r.receiver.id = :id")
  Page<Rating> findByReceiver(@Param("id") Long id, Pageable pageable);

  @Query(value = "SELECT AVG(r.ratingValue) FROM Rating r WHERE r.receiver.id = :id AND r.createdAt > :rewind")
  Double calcAvgRatingValueByReceiver(@Param("id") Long id,@Param("rewind") LocalDateTime rewind);
}
