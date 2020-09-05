package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Rating;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RatingRequest;

public interface RatingService {

  Rating createRating(Long id, String username, RatingRequest request);

  Rating getRating(Long id, String username);

  Page<Rating> getRatingsByContract(Long id, String username, PaginationRequest request);

  Page<Rating> getRatings(PaginationRequest request);

  Page<Rating> getRatingsBySender(String username, PaginationRequest request);

  Page<Rating> getRatingsByReceiver(String username, PaginationRequest request);

  Page<Rating> getRatingsByUser(String username, PaginationRequest request);

  Rating editRating(Long id, String username, Map<String, Object> updates);

  void removeRating(Long id, String username);
}
