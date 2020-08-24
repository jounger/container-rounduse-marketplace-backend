package com.crm.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Outbound;
import com.crm.payload.request.OutboundRequest;
import com.crm.payload.request.PaginationRequest;

public interface OutboundService {

  Outbound getOutboundById(Long id);

  Page<Outbound> getOutbounds(PaginationRequest request);

  Page<Outbound> getOutboundsByMerchant(String username, PaginationRequest request);

  Page<Outbound> searchOutbounds(PaginationRequest request, String search);

  Outbound createOutbound(String username, OutboundRequest request);

  Outbound editOutbound(Map<String, Object> updates, Long id, String username);

  void removeOutbound(Long id, String username);

  List<Outbound> updateExpiredOutboundFromList(List<Outbound> outbounds);

}
