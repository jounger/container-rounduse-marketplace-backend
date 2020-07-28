package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.ShippingLine;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ShippingLineRequest;

public interface ShippingLineService {

  ShippingLine createShippingLine(ShippingLineRequest request);

  ShippingLine getShippingLine(Long id);

  Page<ShippingLine> getShippingLines(PaginationRequest request);

  ShippingLine editShippingLine(Long id, Map<String, Object> updates);

  void removeShippingLine(Long id);
}
