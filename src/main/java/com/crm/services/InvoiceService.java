package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Invoice;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.InvoiceRequest;

public interface InvoiceService {
  
  Invoice createPayment(Long id, String username, InvoiceRequest request);
  
  Page<Invoice> getPaymentsByUser(String username, PaginationRequest request);
  
  Page<Invoice> getPaymentsByContract(Long id, String username, PaginationRequest request);
  
  Page<Invoice> searchPayments(PaginationRequest request, String search);
  
  Invoice editPayment(Long id, String username, Map<String, Object> updates);
  
  void removePayment(Long id, String username);

}
