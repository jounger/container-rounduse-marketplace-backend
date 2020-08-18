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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.Invoice;
import com.crm.models.dto.InvoiceDto;
import com.crm.models.mapper.InvoiceMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.InvoiceRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.InvoiceService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/payment")
public class InvoiceController {

  private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

  @Autowired
  private InvoiceService invoiceService;

  @Transactional
  @PostMapping("/contract/{id}")
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  public ResponseEntity<?> createPayment(@PathVariable("id") Long id, @Valid @RequestBody InvoiceRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Invoice invoice = invoiceService.createPayment(id, username, request);
    InvoiceDto invoiceDto = InvoiceMapper.toPaymentDto(invoice);

    // Set default response body
    DefaultResponse<InvoiceDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_PAYMENT_SUCCESSFULLY);
    defaultResponse.setData(invoiceDto);

    logger.info("User {} createPayment with request: {}", username, request.toString());
    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/contract/{id}")
  public ResponseEntity<?> getPaymentsByContract(@PathVariable("id") Long id, @Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Invoice> pages = invoiceService.getPaymentsByContract(id, username, request);

    PaginationResponse<InvoiceDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Invoice> invoices = pages.getContent();
    List<InvoiceDto> paymentsDto = new ArrayList<>();
    invoices.forEach(payment -> paymentsDto.add(InvoiceMapper.toPaymentDto(payment)));
    response.setContents(paymentsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/user")
  public ResponseEntity<?> getPaymentsByUser(@Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Invoice> pages = invoiceService.getPaymentsByUser(username, request);

    PaginationResponse<InvoiceDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Invoice> invoices = pages.getContent();
    List<InvoiceDto> paymentsDto = new ArrayList<>();
    invoices.forEach(payment -> paymentsDto.add(InvoiceMapper.toPaymentDto(payment)));
    response.setContents(paymentsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/filter")
  public ResponseEntity<?> searchPayments(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {
    Page<Invoice> pages = invoiceService.searchPayments(request, search);
    PaginationResponse<InvoiceDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Invoice> invoices = pages.getContent();
    List<InvoiceDto> paymentsDto = new ArrayList<>();
    invoices.forEach(payment -> paymentsDto.add(InvoiceMapper.toPaymentDto(payment)));
    response.setContents(paymentsDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editPayment(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Invoice invoice = invoiceService.editPayment(id, username, updates);
    InvoiceDto invoiceDto = InvoiceMapper.toPaymentDto(invoice);

    // Set default response body
    DefaultResponse<InvoiceDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_PAYMENT_SUCCESSFULLY);
    defaultResponse.setData(invoiceDto);

    logger.info("User {} editPayment from id {} with request: {}", username, id, updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePayment(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    invoiceService.removePayment(id, username);

    // Set default response body
    DefaultResponse<InvoiceDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_PAYMENT_SUCCESSFULLY);

    logger.info("User {} deletePayment with id {}", username, id);
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
