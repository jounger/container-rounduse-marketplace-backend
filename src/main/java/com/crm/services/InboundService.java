package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Inbound;
import com.crm.payload.request.InboundRequest;
import com.crm.payload.request.PaginationRequest;

public interface InboundService {

  Page<Inbound> getInbounds(PaginationRequest request);

  Inbound getInboundById(Long id);

  Inbound createInbound(Long id, InboundRequest request);

  Inbound updateInbound(InboundRequest request);

  Inbound editInbound(Map<String, Object> updates, Long id);

  void removeInbound(Long id);
}
