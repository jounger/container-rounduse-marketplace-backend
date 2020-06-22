package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Discount;
import com.crm.payload.request.DiscountRequest;
import com.crm.payload.request.PaginationRequest;

public interface DiscountService {

  Page<Discount> getIcds(PaginationRequest request);

  Discount getDiscountById(Long id);

  void createDiscount(DiscountRequest request);

  Discount updateDiscount(DiscountRequest request);

  void removeDiscount(Long id);
}
