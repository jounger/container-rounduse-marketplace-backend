package com.crm.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.crm.models.Contract;
import com.crm.models.ShippingInfo;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ShippingInfoRequest;

public interface ShippingInfoService {

  ShippingInfo createShippingInfo(ShippingInfoRequest request);

  void createShippingInfosForContract(Contract contract, List<Long> containers);

  ShippingInfo getShippingInfo(Long id, String username);

  Page<ShippingInfo> getShippingInfosByBid(Long bidId, String username, PaginationRequest request);

  Page<ShippingInfo> getShippingInfosByCombined(Long combinedId, String username, PaginationRequest request);

  Page<ShippingInfo> getShippingInfosByOutbound(Long outboundId, String username, PaginationRequest request);

  Page<ShippingInfo> getShippingInfosByDriver(String username, PaginationRequest request);

  Page<ShippingInfo> getShippingInfosAreActive(PaginationRequest request);

  ShippingInfo editShippingInfo(Long id, String username, ShippingInfoRequest request);

  void removeShippingInfo(Long id, String username);
}
