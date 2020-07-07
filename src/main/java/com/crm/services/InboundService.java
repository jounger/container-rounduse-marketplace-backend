package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Inbound;
import com.crm.payload.request.InboundRequest;
import com.crm.payload.request.PaginationRequest;

public interface InboundService {

  Inbound getInboundById(Long id);

  Page<Inbound> getInbounds(PaginationRequest request);

  Page<Inbound> getInboundsByOutbound(Long id, PaginationRequest request);

  Page<Inbound> getInboundsByOutboundAndForwarder(Long id, Long userId, PaginationRequest request);

  Page<Inbound> getInboundsForwarder(Long id, PaginationRequest request);

  Inbound createInbound(Long id, InboundRequest request);

  Inbound updateInbound(Long id, InboundRequest request);

  Inbound editInbound(Map<String, Object> updates, Long id);

  void removeInbound(Long id);
}