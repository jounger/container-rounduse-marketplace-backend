package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.BillOfLading;
import com.crm.payload.request.BillOfLadingRequest;
import com.crm.payload.request.PaginationRequest;

public interface BillOfLaingService {

  Page<BillOfLading> getBillOfLadingsByInbound(Long id, PaginationRequest request);

  BillOfLading updateBillOfLading(BillOfLadingRequest request);

  BillOfLading editBillOfLading(Map<String, Object> updates, Long id);
}
