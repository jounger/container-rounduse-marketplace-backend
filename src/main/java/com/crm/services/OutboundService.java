package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Outbound;
import com.crm.payload.request.OutboundRequest;
import com.crm.payload.request.PaginationRequest;

public interface OutboundService {

  Outbound getOutboundById(Long id);

  Page<Outbound> getOutbounds(PaginationRequest request);

  Page<Outbound> getOutboundsByMerchant(Long userId, PaginationRequest request);

  Outbound createOutbound(Long userId, OutboundRequest request);

  Outbound updateOutbound(Long userId, OutboundRequest request);

  Outbound editOutbound(Map<String, Object> updates, Long id, Long userId);

  void removeOutbound(Long id, Long userId);

}
