package com.crm.services;

import com.crm.payload.request.OverviewRequest;
import com.crm.payload.response.ForwarderOverviewResponse;
import com.crm.payload.response.MerchantOverviewResponse;
import com.crm.payload.response.OperatorOverviewResponse;
import com.crm.payload.response.ShippingLineOverviewResponse;

public interface OverviewService {

  public MerchantOverviewResponse getOverviewByMerchant(String username, OverviewRequest request);

  public ForwarderOverviewResponse getOverviewByForwarder(String username, OverviewRequest request);

  public OperatorOverviewResponse getOverviewByOperator(OverviewRequest request);

  public ShippingLineOverviewResponse getOverviewByShippingLine(String username, OverviewRequest request);
}
