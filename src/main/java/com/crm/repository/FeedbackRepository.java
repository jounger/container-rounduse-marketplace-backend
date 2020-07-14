package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Feedback;
import com.crm.models.User;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long>, JpaSpecificationExecutor<Feedback> {

  Page<Feedback> findBySender(User sender, Pageable pageable);

  @Query(value = "FROM Feedback fb WHERE fb.report.id = :id AND (fb.report.sender.username = :username)")
  Page<Feedback> findByReport(@Param("id") Long report, @Param("username") String username, Pageable pageable);

  @Query(value = "FROM Feedback fb WHERE fb.report.id = :id")
  Page<Feedback> findByReport(@Param("id") Long report, Pageable pageable);

}
