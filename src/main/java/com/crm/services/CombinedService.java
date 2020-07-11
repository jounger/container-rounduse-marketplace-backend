package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Combined;
import com.crm.payload.request.CombinedRequest;
import com.crm.payload.request.PaginationRequest;

public interface CombinedService {

  Combined createCombined(CombinedRequest request);

  Combined getCombined(Long id);

  Page<Combined> getCombinedsByBiddingDocument(Long id, Long userId, PaginationRequest request);

  Page<Combined> getCombinedsByUser(Long id, PaginationRequest request);

  Page<Combined> getCombineds(PaginationRequest request);

  Combined updateCombined(CombinedRequest request);

  Combined editCombined(Long id, Map<String, Object> updates);

  void removeCombined(Long id);
}
