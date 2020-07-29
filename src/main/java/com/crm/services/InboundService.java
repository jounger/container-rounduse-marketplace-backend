package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Inbound;
import com.crm.payload.request.InboundRequest;
import com.crm.payload.request.PaginationRequest;

public interface InboundService {

  Inbound getInboundById(Long id);

  Inbound getInboundByContainer(Long id);

  Page<Inbound> getInbounds(PaginationRequest request);

  Page<Inbound> getInboundsByOutbound(Long id, PaginationRequest request);

  Page<Inbound> getInboundsByOutboundAndForwarder(Long id, String username, PaginationRequest request);

  Page<Inbound> getInboundsByForwarder(String username, PaginationRequest request);

  Page<Inbound> searchInbounds(PaginationRequest request, String search);

  Inbound createInbound(String username, InboundRequest request);

  Inbound updateInbound(String username, InboundRequest request);

  Inbound editInbound(Map<String, Object> updates, Long id, String username);

  void removeInbound(Long id, String username);
}