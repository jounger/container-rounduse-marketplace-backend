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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.BillOfLading;
import com.crm.models.dto.BillOfLadingDto;
import com.crm.models.mapper.BillOfLadingMapper;
import com.crm.payload.request.BillOfLadingRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.BillOfLaingService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bill-of-lading")
public class BillOfLadingController {

  @Autowired
  BillOfLaingService billOfLadingService;

  @GetMapping("/inbound/{id}")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getBillOfLadingsByOutbound(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<BillOfLading> pages = billOfLadingService.getBillOfLadingsByInbound(id, request);
    PaginationResponse<BillOfLadingDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<BillOfLading> billOfLadings = pages.getContent();
    List<BillOfLadingDto> BillOfLadingsDto = new ArrayList<>();
    billOfLadings.forEach(billOfLading -> BillOfLadingsDto.add(BillOfLadingMapper.toBillOfLadingDto(billOfLading)));
    response.setContents(BillOfLadingsDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> updateBillOfLading(@Valid @RequestBody BillOfLadingRequest request) {
    BillOfLading billOfLading = billOfLadingService.updateBillOfLading(request);
    BillOfLadingDto billOfLadingDto = BillOfLadingMapper.toBillOfLadingDto(billOfLading);
    return ResponseEntity.ok(billOfLadingDto);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> editBillOfLading(@RequestBody Map<String, Object> updates, @PathVariable("id") Long id) {
    BillOfLading billOfLading = billOfLadingService.editBillOfLading(updates, id);
    BillOfLadingDto billOfLadingDto = new BillOfLadingDto();
    billOfLadingDto = BillOfLadingMapper.toBillOfLadingDto(billOfLading);
    return ResponseEntity.ok(billOfLadingDto);
  }
}
