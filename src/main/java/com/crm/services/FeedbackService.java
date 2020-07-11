package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Feedback;
import com.crm.payload.request.FeedbackRequest;
import com.crm.payload.request.PaginationRequest;

public interface FeedbackService {
  
  Feedback createFeedback(Long id, String username, FeedbackRequest request);
  
  Page<Feedback> getFeedbacksByReport(Long report, String username, PaginationRequest request);

  Page<Feedback> getFeedbacksByUser(String username, PaginationRequest request);

  Page<Feedback> searchFeedbacks(PaginationRequest request, String search);

  Feedback editFeedback(Long id, String username, Map<String, Object> updates);

  void removeFeedback(Long id, String username);
}
