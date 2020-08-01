package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.ShippingInfo;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ShippingInfoRequest;

public interface ShippingInfoService {
  
  ShippingInfo createShippingInfo(ShippingInfoRequest request);
  
  ShippingInfo getShippingInfo(Long id, String username);
  
  Page<ShippingInfo> getShippingInfosByBid(Long bidId, String username, PaginationRequest request);
  
  Page<ShippingInfo> getShippingInfosByOutbound(Long outboundId, String username, PaginationRequest request);
  
  Page<ShippingInfo> getShippingInfosByDriver(String username, PaginationRequest request);
  
  ShippingInfo editShippingInfo(Long id, String username, String status);
  
  void removeShippingInfo(Long id, String username);
}