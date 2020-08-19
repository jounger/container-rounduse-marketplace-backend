package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Invoice;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.InvoiceRequest;

public interface InvoiceService {

  Invoice createInvoice(Long id, String username, InvoiceRequest request);

  Page<Invoice> getInvoicesByUser(String username, PaginationRequest request);

  Page<Invoice> getInvoicesByContract(Long id, String username, PaginationRequest request);

  Page<Invoice> searchInvoices(PaginationRequest request, String search);

  Invoice editInvoice(Long id, String username, Map<String, Object> updates);

  void removeInvoice(Long id, String username);

}
