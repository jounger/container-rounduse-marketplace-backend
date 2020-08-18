package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.crm.models.Merchant;
import com.crm.models.dto.MerchantDto;
import com.crm.models.mapper.MerchantMapper;
import com.crm.payload.request.MerchantRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.MerchantService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

  private static final Logger logger = LoggerFactory.getLogger(MerchantController.class);

  @Autowired
  private MerchantService merchantService;

  @Transactional
  @PostMapping("")
  public ResponseEntity<?> createMerchant(@Valid @RequestBody MerchantRequest request) {
    Merchant merchant = merchantService.createMerchant(request);
    MerchantDto merchantDto = MerchantMapper.toMerchantDto(merchant);

    // Set default response body
    DefaultResponse<MerchantDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_MERCHANT_SUCCESSFULLY);
    defaultResponse.setData(merchantDto);

    logger.info("createMerchant with request: {}", request.toString());
    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @PreAuthorize("hasRole('MODERATOR')")
  @GetMapping("")
  public ResponseEntity<?> getMerchants(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<Merchant> pages = merchantService.getMerchants(request);

    PaginationResponse<MerchantDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Merchant> merchants = pages.getContent();
    List<MerchantDto> merchantsDto = new ArrayList<>();
    merchants.forEach(merchant -> merchantsDto.add(MerchantMapper.toMerchantDto(merchant)));
    response.setContents(merchantsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getMerchant(@PathVariable Long id) {
    Merchant merchant = merchantService.getMerchant(id);
    MerchantDto merchantDto = MerchantMapper.toMerchantDto(merchant);
    return ResponseEntity.ok(merchantDto);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editMerchant(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    Merchant merchant = merchantService.editMerchant(id, updates);
    MerchantDto merchantDto = MerchantMapper.toMerchantDto(merchant);

    // Set default response body
    DefaultResponse<MerchantDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_MERCHANT_SUCCESSFULLY);
    defaultResponse.setData(merchantDto);

    logger.info("editMerchant from id with request: {}", id, updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('MODERATOR')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteMerchant(@PathVariable Long id) {
    merchantService.removeMerchant(id);

    // Set default response body
    DefaultResponse<MerchantDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_MERCHANT_SUCCESSFULLY);

    logger.info("deleteMerchant with id: {}", id);
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
