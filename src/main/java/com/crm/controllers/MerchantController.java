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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Merchant;
import com.crm.models.dto.MerchantDto;
import com.crm.models.mapper.MerchantMapper;
import com.crm.payload.request.MerchantRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.MerchantService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/merchant")
public class MerchantController {
  
  @Autowired
  private MerchantService merchantService;

  @PostMapping("")
  public ResponseEntity<?> createMerchant(@Valid @RequestBody MerchantRequest request) {
    merchantService.saveMerchant(request);
    return ResponseEntity.ok("Shipping Line created successfully");
  }

  @PreAuthorize("hasRole('OPERATOR')")
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

  @PreAuthorize("hasRole('OPERATOR') or hasRole('MERCHANT')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getMerchant(@PathVariable Long id) {
    Merchant merchant = merchantService.getMerchant(id);
    MerchantDto merchantDto = MerchantMapper.toMerchantDto(merchant);
    return ResponseEntity.ok(merchantDto);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT')")
  @PutMapping("")
  public ResponseEntity<?> updateMerchant(@Valid @RequestBody MerchantRequest request) {
    Merchant merchant = merchantService.updateMerchant(request);
    MerchantDto merchantDto = MerchantMapper.toMerchantDto(merchant);
    return ResponseEntity.ok(merchantDto);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editMerchant(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    Merchant merchant = merchantService.editMerchant(id, updates);
    MerchantDto merchantDto = MerchantMapper.toMerchantDto(merchant);
    return ResponseEntity.ok(merchantDto);
  }
  
  @Transactional
  @PreAuthorize("hasRole('OPERATOR')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteMerchant(@PathVariable Long id) {
    merchantService.removeMerchant(id);
    return ResponseEntity.ok(new MessageResponse("Bidding document deleted successfully."));
  }
}
