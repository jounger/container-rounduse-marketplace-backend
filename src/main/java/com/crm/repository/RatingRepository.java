package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crm.models.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long>{

  @Query(value = "FROM Rating r WHERE r.sender.id = :id")
  Page<Rating> findBySenderId(@Param("id") Long id, Pageable pageable);
  
  @Query(value = "FROM Rating r WHERE r.receiver.id = :id")
  Page<Rating> findByReceiverId(@Param("id") Long id, Pageable pageable);
  
  @Query(value = "SELECT AVG(r.ratingValue) FROM Rating r WHERE r.receiver.id = :id")
  Float findAvgRatingValueByReceiverId(@Param("id") Long id);
}
