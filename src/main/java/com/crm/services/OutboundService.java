package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Outbound;
import com.crm.payload.request.OutboundRequest;
import com.crm.payload.request.PaginationRequest;

public interface OutboundService {

  Outbound getOutboundById(Long id);

  Page<Outbound> getOutbounds(PaginationRequest request);

  Page<Outbound> getOutboundsByMerchant(Long id, PaginationRequest request);

  Outbound createOutbound(Long id, OutboundRequest request);

  Outbound updateOutbound(OutboundRequest request);

  Outbound editOutbound(Map<String, Object> updates, Long id);

  void removeOutbound(Long id);

}
