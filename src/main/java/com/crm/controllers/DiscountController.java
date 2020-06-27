package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
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

import com.crm.models.Discount;
import com.crm.models.dto.DiscountDto;
import com.crm.models.mapper.DiscountMapper;
import com.crm.payload.request.DiscountRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.DiscountService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/discount")
public class DiscountController {

  @Autowired
  private DiscountService discountService;

  @GetMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> getDiscounts(@Valid PaginationRequest request) {

    Page<Discount> pages = discountService.getDiscounts(request);
    PaginationResponse<DiscountDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Discount> discounts = pages.getContent();
    List<DiscountDto> discountDto = new ArrayList<>();
    discounts.forEach(discount -> discountDto.add(DiscountMapper.toDiscountDto(discount)));
    response.setContents(discountDto);

    return ResponseEntity.ok(response);

  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> getDiscount(@PathVariable Long id) {
    Discount discount = discountService.getDiscountById(id);
    DiscountDto discountDto = new DiscountDto();
    discountDto = DiscountMapper.toDiscountDto(discount);
    return ResponseEntity.ok(discountDto);
  }

  @PostMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> createDiscount(@Valid @RequestBody DiscountRequest request) {
    Discount discount = discountService.createDiscount(request);
    DiscountDto discountDto = DiscountMapper.toDiscountDto(discount);
    return ResponseEntity.ok(discountDto);
  }

  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> updateDiscount(@Valid @RequestBody DiscountRequest request) {
    Discount discount = discountService.updateDiscount(request);
    DiscountDto discountDto = DiscountMapper.toDiscountDto(discount);
    return ResponseEntity.ok(discountDto);
  }

  @PreAuthorize("hasRole('MODERATOR')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editDiscount(@RequestBody Map<String, Object> updates, @PathVariable("id") Long id) {
    Discount discount = discountService.editDiscount(updates, id);
    DiscountDto discountDto = new DiscountDto();
    discountDto = DiscountMapper.toDiscountDto(discount);
    return ResponseEntity.ok(discountDto);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> removeDiscount(@PathVariable Long id) {
    discountService.removeDiscount(id);
    return ResponseEntity.ok(new MessageResponse("Discount has remove successfully"));
  }
}
