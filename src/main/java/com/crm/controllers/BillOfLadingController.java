package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.BillOfLading;
import com.crm.models.dto.BillOfLadingDto;
import com.crm.models.mapper.BillOfLadingMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.BillOfLadingService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bill-of-lading")
public class BillOfLadingController {

  @Autowired
  BillOfLadingService billOfLadingService;

  @GetMapping("/inbound/{id}")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getBillOfLadingByInbound(@PathVariable Long id, @Valid PaginationRequest request) {

    BillOfLading billOfLading = billOfLadingService.getBillOfLadingByInbound(id);
    BillOfLadingDto billOfLadingDto = new BillOfLadingDto();
    billOfLadingDto = BillOfLadingMapper.toBillOfLadingDto(billOfLading);
    return ResponseEntity.ok(billOfLadingDto);
  }

  @GetMapping("/filter")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> searchBillOfLadings(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {

    Page<BillOfLading> pages = billOfLadingService.searchBillOfLadings(request, search);
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

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getBillOfLading(@PathVariable Long id) {
    BillOfLading billOfLading = billOfLadingService.getBillOfLadingById(id);
    BillOfLadingDto billOfLadingDto = new BillOfLadingDto();
    billOfLadingDto = BillOfLadingMapper.toBillOfLadingDto(billOfLading);
    return ResponseEntity.ok(billOfLadingDto);
  }

  @RequestMapping(method = RequestMethod.GET, params = { "number" })
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getBillOfLadingByNumber(@RequestParam String number) {
    BillOfLading billOfLading = billOfLadingService.getBillOfLadingByNumber(number);
    BillOfLadingDto billOfLadingDto = new BillOfLadingDto();
    billOfLadingDto = BillOfLadingMapper.toBillOfLadingDto(billOfLading);
    return ResponseEntity.ok(billOfLadingDto);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> editBillOfLading(@RequestBody Map<String, Object> updates, @PathVariable("id") Long id) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    BillOfLading billOfLading = billOfLadingService.editBillOfLading(updates, id, username);
    BillOfLadingDto billOfLadingDto = new BillOfLadingDto();
    billOfLadingDto = BillOfLadingMapper.toBillOfLadingDto(billOfLading);

    // Set default response body
    DefaultResponse<BillOfLadingDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_BILL_OF_LADING_SUCCESSFULLY);
    defaultResponse.setData(billOfLadingDto);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
