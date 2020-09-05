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
import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.ForbiddenException;
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
  public Rating createRating(Long id, String username, RatingRequest request) {
    Rating rating = new Rating();

    Contract contract = contractRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.SENDER_NOT_FOUND));
    rating.setContract(contract);
    if (!contractRepository.existsByUserAndContract(id, username)) {
      throw new DuplicateRecordException(ErrorMessage.USER_ACCESS_DENIED);
    }

    Supplier merchant = contract.getSender();
    Supplier forwarder = contract.getCombined().getBid().getBidder();
    if (username.equals(merchant.getUsername())) {
      rating.setSender(merchant);
      rating.setReceiver(forwarder);
    } else if (username.equals(forwarder.getUsername())) {
      rating.setSender(forwarder);
      rating.setReceiver(merchant);
    } else {
      throw new NotFoundException(ErrorMessage.USER_NOT_FOUND);
    }

    if (ratingRepository.existsByUserAndContract(id, username)) {
      throw new ForbiddenException(ErrorMessage.RATING_ONE_PER_CONTRACT);
    }
    rating.setRatingValue(request.getRatingValue());
    rating.setComment(request.getComment());
    Rating _rating = ratingRepository.save(rating);

    LocalDateTime rewind = LocalDateTime.now().minusMonths(Constant.REWIND_MONTH);
    Double ratingValue = ratingRepository.calcAvgRatingValueByReceiver(rating.getReceiver().getId(),
        Timestamp.valueOf(rewind));
    rating.getReceiver().setRatingValue(ratingValue);
    supplierRepository.save(rating.getReceiver());

    return _rating;
  }

  @Override
  public Rating getRating(Long id, String username) {
    Rating rating = ratingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.RATING_NOT_FOUND));
    if (!(rating.getSender().getUsername().equals(username) || rating.getReceiver().getUsername().equals(username))) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
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
  public Page<Rating> getRatingsByContract(Long id, String username, PaginationRequest request) {
    Page<Rating> ratings = null;
    Contract contract = contractRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTRACT_NOT_FOUND));
    Bid bid = contract.getCombined().getBid();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    if (bid.getBidder().getUsername().equals(username) || biddingDocument.getOfferee().getUsername().equals(username)) {
      PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "createdAt"));
      LocalDateTime rewind = LocalDateTime.now().minusMonths(Constant.REWIND_MONTH);
      ratings = ratingRepository.findByContract(id, username, Timestamp.valueOf(rewind), page);
    } else {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
    return ratings;
  }

  @Override
  public Page<Rating> getRatingsBySender(String username, PaginationRequest request) {
    if (!supplierRepository.existsByUsername(username)) {
      throw new NotFoundException(ErrorMessage.SENDER_NOT_FOUND);
    }
    LocalDateTime rewind = LocalDateTime.now().minusMonths(Constant.REWIND_MONTH);
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "createdAt"));
    Page<Rating> ratings = ratingRepository.findBySender(username, Timestamp.valueOf(rewind), page);
    return ratings;
  }

  @Override
  public Page<Rating> getRatingsByReceiver(String username, PaginationRequest request) {
    if (!supplierRepository.existsByUsername(username)) {
      throw new NotFoundException(ErrorMessage.RECIPIENT_NOT_FOUND);
    }
    LocalDateTime rewind = LocalDateTime.now().minusMonths(Constant.REWIND_MONTH);
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "createdAt"));
    Page<Rating> ratings = ratingRepository.findByReceiver(username, Timestamp.valueOf(rewind), page);
    return ratings;
  }

  @Override
  public Page<Rating> getRatingsByUser(String username, PaginationRequest request) {
    if (!supplierRepository.existsByUsername(username)) {
      throw new NotFoundException(ErrorMessage.USER_NOT_FOUND);
    }
    LocalDateTime rewind = LocalDateTime.now().minusMonths(Constant.REWIND_MONTH);
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "createdAt"));
    Page<Rating> ratings = ratingRepository.findByUser(username, Timestamp.valueOf(rewind), page);
    return ratings;
  }

  @Override
  public Rating editRating(Long id, String username, Map<String, Object> updates) {
    Rating rating = ratingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.RATING_NOT_FOUND));

    if (!rating.getSender().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    String ratingValue = String.valueOf(updates.get("ratingValue"));
    if (updates.get("ratingValue") != null && !Tool.isEqual(rating.getRatingValue(), ratingValue)) {
      rating.setRatingValue(Integer.valueOf(ratingValue));
    }
    String comment = String.valueOf(updates.get("comment"));
    if (updates.get("comment") != null && !Tool.isEqual(rating.getComment(), comment)) {
      rating.setComment(comment);
    }
    Rating _rating = ratingRepository.save(rating);

    Supplier receiver = rating.getReceiver();
    LocalDateTime rewind = LocalDateTime.now().minusMonths(Constant.REWIND_MONTH);
    Double receiverRatingValue = ratingRepository.calcAvgRatingValueByReceiver(receiver.getId(),
        Timestamp.valueOf(rewind));
    receiver.setRatingValue(receiverRatingValue);
    supplierRepository.save(receiver);

    return _rating;
  }

  @Override
  public void removeRating(Long id, String username) {
    Rating rating = ratingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.RATING_NOT_FOUND));
    if (rating.getSender().getUsername().equals(username)) {
      ratingRepository.deleteById(id);
    } else {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
    Supplier receiver = rating.getReceiver();
    LocalDateTime rewind = LocalDateTime.now().minusMonths(Constant.REWIND_MONTH);
    Double receiverRatingValue = ratingRepository.calcAvgRatingValueByReceiver(receiver.getId(),
        Timestamp.valueOf(rewind));
    receiver.setRatingValue(receiverRatingValue);
    supplierRepository.save(receiver);
  }

}
