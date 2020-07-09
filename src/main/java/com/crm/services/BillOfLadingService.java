package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.BillOfLading;
import com.crm.payload.request.BillOfLadingRequest;
import com.crm.payload.request.PaginationRequest;

public interface BillOfLadingService {

  Page<BillOfLading> getBillOfLadingsByInbound(Long id, PaginationRequest request);

  BillOfLading getBillOfLadingById(Long id);

  BillOfLading getBillOfLadingByBillOfLadingNumber(String billOfLadingNumber);

  Page<BillOfLading> searchBillOfLadings(PaginationRequest request, String search);

  BillOfLading updateBillOfLading(Long userId, BillOfLadingRequest request);

  BillOfLading editBillOfLading(Map<String, Object> updates, Long id, Long userId);
}
