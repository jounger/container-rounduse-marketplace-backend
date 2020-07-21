package com.crm.services.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.common.ErrorConstant;
import com.crm.common.Tool;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Contract;
import com.crm.models.Rating;
import com.crm.models.Supplier;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RatingRequest;
import com.crm.repository.ContractRepository;
import com.crm.repository.RatingRepository;
import com.crm.repository.SupplierRepository;
import com.crm.services.RatingService;

@Service
public class RatingServiceImpl implements RatingService {

  @Autowired
  private RatingRepository ratingRepository;

  @Autowired
  private SupplierRepository supplierRepository;

  @Autowired
  private ContractRepository contractRepository;

  @Override
  public Rating createRating(Long id, RatingRequest request) {
    Rating rating = new Rating();

    Supplier sender = supplierRepository.findById(id).orElseThrow(() -> new NotFoundException("Sender is not found."));
    rating.setSender(sender);

    Supplier receiver = supplierRepository.findById(request.getReceiver())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.RECIPIENT_NOT_FOUND));
    rating.setReceiver(receiver);

    Contract contract = contractRepository.findById(request.getContract())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.SENDER_NOT_FOUND));
    rating.setContract(contract);
    if (!contractRepository.existsByUserAndContract(request.getContract(), id)) {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }

    if (ratingRepository.existsByUserAndContract(request.getContract(), id)) {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }
    rating.setRatingValue(request.getRatingValue());
    ratingRepository.save(rating);

    LocalDateTime rewind = LocalDateTime.now().minusMonths(Constant.REWIND_MONTH);
    Double ratingValue = ratingRepository.calcAvgRatingValueByReceiver(request.getReceiver(),
        Timestamp.valueOf(rewind));
    receiver.setRatingValue(ratingValue);
    supplierRepository.save(receiver);

    supplierRepository.save(receiver);
    return rating;
  }

  @Override
  public Rating getRating(Long id, Long userId) {
    Rating rating = ratingRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorConstant.RATING_NOT_FOUND));
    if (rating.getSender().getId() != userId || rating.getReceiver().getId() != userId) {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }
    return rating;
  }

  @Override
  public Page<Rating> getRatings(PaginationRequest request) {
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "createdAt"));
    Page<Rating> ratings = ratingRepository.findAll(page);
    return ratings;
  }

  @Override
  public Page<Rating> getRatingsByContract(Long id, Long userId, PaginationRequest request) {
    Page<Rating> ratings = null;
    Contract contract = contractRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.CONTRACT_NOT_FOUND));
    Bid bid = contract.getCombined().getBid();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    if (bid.getBidder().getId() == userId || biddingDocument.getOfferee().getId() == userId) {
      PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "createdAt"));
      LocalDateTime rewind = LocalDateTime.now().minusMonths(Constant.REWIND_MONTH);
      ratings = ratingRepository.findByContract(id, userId, Timestamp.valueOf(rewind), page);
    } else {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }
    return ratings;
  }

  @Override
  public Page<Rating> getRatingsBySender(Long id, PaginationRequest request) {
    if (!supplierRepository.existsById(id)) {
      throw new NotFoundException(ErrorConstant.SENDER_NOT_FOUND);
    }
    LocalDateTime rewind = LocalDateTime.now().minusMonths(Constant.REWIND_MONTH);
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "createdAt"));
    Page<Rating> ratings = ratingRepository.findBySender(id, Timestamp.valueOf(rewind), page);
    return ratings;
  }

  @Override
  public Page<Rating> getRatingsByReceiver(Long id, PaginationRequest request) {
    if (!supplierRepository.existsById(id)) {
      throw new NotFoundException(ErrorConstant.RECIPIENT_NOT_FOUND);
    }
    LocalDateTime rewind = LocalDateTime.now().minusMonths(Constant.REWIND_MONTH);
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "createdAt"));
    Page<Rating> ratings = ratingRepository.findByReceiver(id, Timestamp.valueOf(rewind), page);
    return ratings;
  }

  @Override
  public Page<Rating> getRatingsByUser(Long id, PaginationRequest request) {
    if (!supplierRepository.existsById(id)) {
      throw new NotFoundException(ErrorConstant.USER_NOT_FOUND);
    }
    LocalDateTime rewind = LocalDateTime.now().minusMonths(Constant.REWIND_MONTH);
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "createdAt"));
    Page<Rating> ratings = ratingRepository.findByUser(id, Timestamp.valueOf(rewind), page);
    return ratings;
  }

  @Override
  public Rating updateRating(Long id, Long userId, RatingRequest request) {
    Rating rating = ratingRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.RATING_NOT_FOUND));

    Supplier sender = supplierRepository.findById(request.getSender())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.SENDER_NOT_FOUND));
    rating.setSender(sender);

    Supplier receiver = supplierRepository.findById(request.getSender())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.RECIPIENT_NOT_FOUND));
    rating.setReceiver(receiver);

    rating.setRatingValue(request.getRatingValue());
    ratingRepository.save(rating);

    LocalDateTime rewind = LocalDateTime.now().minusMonths(Constant.REWIND_MONTH);
    Double ratingValue = ratingRepository.calcAvgRatingValueByReceiver(request.getReceiver(),
        Timestamp.valueOf(rewind));
    receiver.setRatingValue(ratingValue);

    supplierRepository.save(receiver);

    return rating;
  }

  @Override
  public Rating editRating(Long id, Long userId, Map<String, Object> updates) {
    Rating rating = ratingRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorConstant.RATING_NOT_FOUND));

    if (rating.getSender().getId() != userId) {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }

    String ratingValue = String.valueOf(updates.get("ratingValue"));
    if (updates.get("ratingValue") != null && !Tool.isEqual(rating.getRatingValue(), ratingValue)) {
      rating.setRatingValue(Integer.valueOf(ratingValue));
      ratingRepository.save(rating);

      Supplier receiver = rating.getReceiver();
      LocalDateTime rewind = LocalDateTime.now().minusMonths(Constant.REWIND_MONTH);
      Double receiverRatingValue = ratingRepository.calcAvgRatingValueByReceiver(receiver.getId(),
          Timestamp.valueOf(rewind));
      receiver.setRatingValue(receiverRatingValue);
      supplierRepository.save(receiver);
    }

    return rating;
  }

  @Override
  public void removeRating(Long id, Long userId) {
    Rating rating = ratingRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorConstant.RATING_NOT_FOUND));
    if (rating.getSender().getId() == userId) {
      ratingRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }
    Supplier receiver = rating.getReceiver();
    LocalDateTime rewind = LocalDateTime.now().minusMonths(Constant.REWIND_MONTH);
    Double receiverRatingValue = ratingRepository.calcAvgRatingValueByReceiver(receiver.getId(),
        Timestamp.valueOf(rewind));
    receiver.setRatingValue(receiverRatingValue);
    supplierRepository.save(receiver);
  }

}
