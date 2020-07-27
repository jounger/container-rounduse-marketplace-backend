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

import com.crm.models.Payment;
import com.crm.models.dto.PaymentDto;
import com.crm.models.mapper.PaymentMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.PaymentRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.PaymentService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

  @Autowired
  private PaymentService paymentService;

  @Transactional
  @PostMapping("/contract/{id}")
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  public ResponseEntity<?> createPayment(@PathVariable("id") Long id, @Valid @RequestBody PaymentRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Payment payment = paymentService.createPayment(id, username, request);
    PaymentDto paymentDto = PaymentMapper.toPaymentDto(payment);
    return ResponseEntity.ok(paymentDto);
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/contract/{id}")
  public ResponseEntity<?> getPaymentsByContract(@PathVariable("id") Long id, @Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();

    Page<Payment> pages = paymentService.getPaymentsByContract(id, username, request);

    PaginationResponse<PaymentDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Payment> payments = pages.getContent();
    List<PaymentDto> paymentsDto = new ArrayList<>();
    payments.forEach(payment -> paymentsDto.add(PaymentMapper.toPaymentDto(payment)));
    response.setContents(paymentsDto);

    return ResponseEntity.ok(response);
  }
  
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/user")
  public ResponseEntity<?> getPaymentsByUser(@Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Payment> pages = paymentService.getPaymentsByUser(username, request);

    PaginationResponse<PaymentDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Payment> payments = pages.getContent();
    List<PaymentDto> paymentsDto = new ArrayList<>();
    payments.forEach(payment -> paymentsDto.add(PaymentMapper.toPaymentDto(payment)));
    response.setContents(paymentsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/filter")
  public ResponseEntity<?> searchPayments(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {
    Page<Payment> pages = paymentService.searchPayments(request, search);
    PaginationResponse<PaymentDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Payment> payments = pages.getContent();
    List<PaymentDto> paymentsDto = new ArrayList<>();
    payments.forEach(payment -> paymentsDto.add(PaymentMapper.toPaymentDto(payment)));
    response.setContents(paymentsDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editPayment(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Payment payment = paymentService.editPayment(id, username, updates);
    PaymentDto paymentDto = PaymentMapper.toPaymentDto(payment);
    return ResponseEntity.ok(paymentDto);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePayment(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    paymentService.removePayment(id, username);
    return ResponseEntity.ok(new MessageResponse("Bidding document deleted successfully."));
  }
}
