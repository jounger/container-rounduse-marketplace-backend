package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Forwarder;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SupplierRequest;

public interface ForwarderService {

  Forwarder createForwarder(SupplierRequest request);

  Forwarder getForwarder(Long id);

  Page<Forwarder> getForwarders(PaginationRequest request);

  Page<Forwarder> findForwardersByOutbound(Long id, PaginationRequest request);

  Forwarder editForwarder(Long id, Map<String, Object> updates);

  void removeForwarder(Long id);
}
