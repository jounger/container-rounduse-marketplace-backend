package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Combined;
import com.crm.payload.request.CombinedRequest;
import com.crm.payload.request.PaginationRequest;

public interface CombinedService {

  Combined createCombined(Long bidId, String username, CombinedRequest request);

  Combined getCombined(Long id);

  Page<Combined> getCombinedsByBiddingDocument(Long id, String username, PaginationRequest request);

  Page<Combined> getCombinedsByUser(String username, PaginationRequest request);

  Page<Combined> getCombineds(PaginationRequest request);

  Combined editCombined(Long id, String username, String isCanceled);

  void removeCombined(Long id);
}
