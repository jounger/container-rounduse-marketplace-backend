package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.BillOfLading;
import com.crm.payload.request.BillOfLadingRequest;
import com.crm.payload.request.PaginationRequest;

public interface BillOfLadingService {

  BillOfLading getBillOfLadingByInbound(Long id);

  BillOfLading getBillOfLadingById(Long id);

  BillOfLading getBillOfLadingByNumber(String number);

  Page<BillOfLading> searchBillOfLadings(PaginationRequest request, String search);

  BillOfLading updateBillOfLading(String username, BillOfLadingRequest request);

  BillOfLading editBillOfLading(Map<String, Object> updates, Long id, String username);
}
