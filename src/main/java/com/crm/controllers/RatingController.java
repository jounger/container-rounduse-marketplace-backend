package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.Rating;
import com.crm.models.dto.RatingDto;
import com.crm.models.mapper.RatingMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RatingRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
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
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Rating rating = ratingService.createRating(username, request);
    RatingDto ratingDto = RatingMapper.toRatingDto(rating);

    // Set default response body
    DefaultResponse<RatingDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_RATING_SUCCESSFULLY);
    defaultResponse.setData(ratingDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/sender")
  public ResponseEntity<?> getRatingsBySender(@Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Rating> pages = ratingService.getRatingsBySender(username, request);

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
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Rating> pages = ratingService.getRatingsByReceiver(username, request);

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
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Rating> pages = ratingService.getRatingsByUser(username, request);

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
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Rating> pages = ratingService.getRatingsByContract(id, username, request);

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
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Rating rating = ratingService.editRating(id, username, updates);
    RatingDto ratingDto = RatingMapper.toRatingDto(rating);

    // Set default response body
    DefaultResponse<RatingDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_RATING_SUCCESSFULLY);
    defaultResponse.setData(ratingDto);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteRating(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    ratingService.removeRating(id, username);

    // Set default response body
    DefaultResponse<RatingDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_RATING_SUCCESSFULLY);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
