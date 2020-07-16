package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Rating;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RatingRequest;

public interface RatingService {

  Rating createRating(Long id, RatingRequest request);

  Rating getRating(Long id, Long userId);

  Page<Rating> getRatingsByContract(Long id, Long userId, PaginationRequest request);

  Page<Rating> getRatings(PaginationRequest request);

  Page<Rating> getRatingsBySender(Long id, PaginationRequest request);

  Page<Rating> getRatingsByReceiver(Long id, PaginationRequest request);

  Page<Rating> getRatingsByUser(Long id, PaginationRequest request);

  Rating updateRating(Long id, Long userId, RatingRequest request);

  Rating editRating(Long id, Long userId, Map<String, Object> updates);

  void removeRating(Long id, Long userId);
}
