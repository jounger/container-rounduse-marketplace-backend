package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Payment;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.PaymentRequest;

public interface PaymentService {
  
  Payment createPayment(String username, PaymentRequest request);
  
  Page<Payment> getPaymentsByUser(String username, PaginationRequest request);
  
  Page<Payment> searchPayments(PaginationRequest request, String search);
  
  Payment editPayment(Long id, String username, Map<String, Object> updates);
  
  void removePayment(Long id, String username);

}
