package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.Combined;
import com.crm.models.dto.CombinedDto;
import com.crm.models.mapper.CombinedMapper;
import com.crm.payload.request.CombinedRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.CombinedService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/combined")
public class CombinedController {

  private static final Logger logger = LoggerFactory.getLogger(CombinedController.class);

  @Autowired
  private CombinedService combinedService;

  @Autowired
  @Qualifier("cachedThreadPool")
  private ExecutorService executorService;

  @Transactional
  @PreAuthorize("hasRole('MERCHANT')")
  @PostMapping("/bid/{id}")
  public ResponseEntity<?> createCombined(@PathVariable("id") Long id, @Valid @RequestBody CombinedRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Combined combined = combinedService.createCombined(id, username, request);
    CombinedDto combinedDto = CombinedMapper.toCombinedDto(combined);

    // Set default response body
    DefaultResponse<CombinedDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_BID_SUCCESSFULLY);
    defaultResponse.setData(combinedDto);

    logger.info("User {} createCombined with request: {}", username, request.toString());
    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getCombined(@PathVariable("id") Long id) {
    Combined combined = combinedService.getCombined(id);
    CombinedDto combinedDto = CombinedMapper.toCombinedDto(combined);
    return ResponseEntity.ok(combinedDto);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("")
  public ResponseEntity<?> getCombineds(@Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Combined> pages = combinedService.getCombinedsByUser(username, request);

    PaginationResponse<CombinedDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Combined> combineds = pages.getContent();
    List<CombinedDto> combinedsDto = new ArrayList<>();
    combineds.forEach(combined -> combinedsDto.add(CombinedMapper.toCombinedDto(combined)));
    response.setContents(combinedsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/bidding-document/{id}")
  public ResponseEntity<?> getCombinedsByBiddingDocument(@PathVariable("id") Long id,
      @Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Combined> pages = combinedService.getCombinedsByBiddingDocument(id, username, request);

    PaginationResponse<CombinedDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Combined> combineds = pages.getContent();
    List<CombinedDto> combinedsDto = new ArrayList<>();
    combineds.forEach(combined -> combinedsDto.add(CombinedMapper.toCombinedDto(combined)));
    response.setContents(combinedsDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT') or hasRole('DRIVER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editCombined(@PathVariable("id") Long id, @RequestBody CombinedRequest isCanceled) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Combined combined = combinedService.editCombined(id, username, isCanceled.getIsCanceled().toString());
    CombinedDto combinedDto = CombinedMapper.toCombinedDto(combined);

    // Set default response body
    DefaultResponse<CombinedDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_BID_SUCCESSFULLY);
    defaultResponse.setData(combinedDto);

    logger.info("User {} editCombined from id {} with request: {}", username, id,
        isCanceled.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
