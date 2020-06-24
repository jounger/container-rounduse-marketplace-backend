package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Rating;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RatingRequest;

public interface RatingService {
  
  void createRating(RatingRequest request);
  
  Rating getRating(Long id);
  
  Page<Rating> getRatings(PaginationRequest request);
  
  Page<Rating> getRatingsBySender(Long id, PaginationRequest request);
  
  Page<Rating> getRatingsByReceiver(Long id, PaginationRequest request);
  
  Rating updateRating(RatingRequest request);
  
  Rating editRating(Long id, Map<String, Object> updates);
  
  void removeRating(Long id);
}
