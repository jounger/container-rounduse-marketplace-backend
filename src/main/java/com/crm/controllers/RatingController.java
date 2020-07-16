package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Rating;
import com.crm.models.dto.RatingDto;
import com.crm.models.mapper.RatingMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RatingRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.security.services.UserDetailsImpl;
import com.crm.services.RatingService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/rating")
public class RatingController {

  @Autowired
  private RatingService ratingService;

  @Transactional
  @PostMapping("")
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  public ResponseEntity<?> createRating(@Valid @RequestBody RatingRequest request) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();
    Rating rating = ratingService.createRating(userId, request);
    RatingDto ratingDto = RatingMapper.toRatingDto(rating);
    return ResponseEntity.ok(ratingDto);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/sender")
  public ResponseEntity<?> getRatingsBySender(@Valid PaginationRequest request) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    Page<Rating> pages = ratingService.getRatingsBySender(userId, request);

    PaginationResponse<RatingDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Rating> ratings = pages.getContent();
    List<RatingDto> ratingsDto = new ArrayList<>();
    ratings.forEach(rating -> ratingsDto.add(RatingMapper.toRatingDto(rating)));
    response.setContents(ratingsDto);

    return ResponseEntity.ok(response);
  }
  
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/receiver")
  public ResponseEntity<?> getRatingsByReceiver(@Valid PaginationRequest request) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    Page<Rating> pages = ratingService.getRatingsByReceiver(userId, request);

    PaginationResponse<RatingDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Rating> ratings = pages.getContent();
    List<RatingDto> ratingsDto = new ArrayList<>();
    ratings.forEach(rating -> ratingsDto.add(RatingMapper.toRatingDto(rating)));
    response.setContents(ratingsDto);

    return ResponseEntity.ok(response);
  }
  
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/user")
  public ResponseEntity<?> getRatingsByUser(@Valid PaginationRequest request) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    Page<Rating> pages = ratingService.getRatingsByUser(userId, request);

    PaginationResponse<RatingDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Rating> ratings = pages.getContent();
    List<RatingDto> ratingsDto = new ArrayList<>();
    ratings.forEach(rating -> ratingsDto.add(RatingMapper.toRatingDto(rating)));
    response.setContents(ratingsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/contract/{id}")
  public ResponseEntity<?> getRatingsByContract(@PathVariable("id") Long id, @Valid PaginationRequest request) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    Page<Rating> pages = ratingService.getRatingsByContract(id, userId, request);

    PaginationResponse<RatingDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Rating> ratings = pages.getContent();
    List<RatingDto> ratingsDto = new ArrayList<>();
    ratings.forEach(rating -> ratingsDto.add(RatingMapper.toRatingDto(rating)));
    response.setContents(ratingsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MODERATOR')")
  @GetMapping("")
  public ResponseEntity<?> getRatings(@Valid PaginationRequest request) {
    Page<Rating> pages = ratingService.getRatings(request);
    PaginationResponse<RatingDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Rating> ratings = pages.getContent();
    List<RatingDto> ratingsDto = new ArrayList<>();
    ratings.forEach(rating -> ratingsDto.add(RatingMapper.toRatingDto(rating)));
    response.setContents(ratingsDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editRating(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();
    Rating rating = ratingService.editRating(id, userId, updates);
    RatingDto ratingDto = RatingMapper.toRatingDto(rating);
    return ResponseEntity.ok(ratingDto);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteRating(@PathVariable Long id) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();
    ratingService.removeRating(id, userId);
    return ResponseEntity.ok(new MessageResponse("Rating deleted successfully."));
  }
}
