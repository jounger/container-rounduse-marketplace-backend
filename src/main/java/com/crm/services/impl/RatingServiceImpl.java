package com.crm.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.crm.exception.NotFoundException;
import com.crm.models.Rating;
import com.crm.models.Supplier;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RatingRequest;
import com.crm.repository.RatingRepository;
import com.crm.repository.SupplierRepository;
import com.crm.services.RatingService;

@Service
public class RatingServiceImpl implements RatingService {

  @Autowired
  private RatingRepository ratingRepository;

  @Autowired
  private SupplierRepository supplierRepository;

  @Override
  public void createRating(RatingRequest request) {
    Rating rating = new Rating();

    Supplier sender = supplierRepository.findById(request.getSenderId())
        .orElseThrow(() -> new NotFoundException("Sender is not found."));
    rating.setSender(sender);

    Supplier receiver = supplierRepository.findById(request.getSenderId())
        .orElseThrow(() -> new NotFoundException("Receiver is not found."));
    rating.setReceiver(receiver);

    rating.setRatingValue(request.getRatingValue());
    ratingRepository.save(rating);

    Double ratingValue = ratingRepository.findAvgRatingValueByReceiverId(request.getReceiverId());
    receiver.setRatingValue(ratingValue);
    
    supplierRepository.save(receiver);
  }

  @Override
  public Rating getRating(Long id) {
    Rating rating = ratingRepository.findById(id).orElseThrow(() -> new NotFoundException("Rating is not found."));
    return rating;
  }

  @Override
  public Page<Rating> getRatings(PaginationRequest request) {
    Page<Rating> ratings = ratingRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return ratings;
  }

  @Override
  public Page<Rating> getRatingsBySender(Long id, PaginationRequest request) {
    Page<Rating> ratings = ratingRepository.findBySenderId(id, PageRequest.of(request.getPage(), request.getLimit()));
    return ratings;
  }

  @Override
  public Page<Rating> getRatingsByReceiver(Long id, PaginationRequest request) {
    Page<Rating> ratings = ratingRepository.findByReceiverId(id, PageRequest.of(request.getPage(), request.getLimit()));
    return ratings;
  }

  @Override
  public Rating updateRating(RatingRequest request) {
    Rating rating = ratingRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("Rating is not found."));

    Supplier sender = supplierRepository.findById(request.getSenderId())
        .orElseThrow(() -> new NotFoundException("Sender is not found."));
    rating.setSender(sender);

    Supplier receiver = supplierRepository.findById(request.getSenderId())
        .orElseThrow(() -> new NotFoundException("Receiver is not found."));
    rating.setReceiver(receiver);

    rating.setRatingValue(request.getRatingValue());
    ratingRepository.save(rating);

    Double ratingValue = ratingRepository.findAvgRatingValueByReceiverId(request.getReceiverId());
    receiver.setRatingValue(ratingValue);
    
    supplierRepository.save(receiver);

    return rating;
  }

  @Override
  public Rating editRating(Long id, Map<String, Object> updates) {
    Rating rating = ratingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Rating is not found."));
    
    Long senderId = (Long)updates.get("senderId");
    if(senderId != null) {
      Supplier sender = supplierRepository.findById(senderId)
          .orElseThrow(() -> new NotFoundException("Sender is not found."));
      rating.setSender(sender);
    }
    
    Long receiverId = (Long)updates.get("receiverId");
    Supplier receiver = new Supplier();
    if(senderId != null) {
      receiver = supplierRepository.findById(receiverId)
          .orElseThrow(() -> new NotFoundException("Receiver is not found."));
      rating.setSender(receiver);
    }
    
    Integer ratingValue = (Integer)updates.get("ratingValue");
    if(ratingValue != null) {
      rating.setRatingValue(ratingValue);
      ratingRepository.save(rating);
      
      Double receiverRatingValue = ratingRepository.findAvgRatingValueByReceiverId(receiverId);
      receiver.setRatingValue(receiverRatingValue);     
      supplierRepository.save(receiver);
    }   
    
    return rating;
  }

  @Override
  public void removeRating(Long id) {
    if (ratingRepository.existsById(id)) {
      ratingRepository.deleteById(id);
    } else {
      throw new NotFoundException("Rating is not found.");
    }
  }

}
