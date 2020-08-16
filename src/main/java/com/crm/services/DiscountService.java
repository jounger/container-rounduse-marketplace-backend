package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Discount;
import com.crm.payload.request.DiscountRequest;
import com.crm.payload.request.PaginationRequest;

public interface DiscountService {

  Page<Discount> getDiscounts(PaginationRequest request);

  Discount getDiscountById(Long id);

  Discount getDiscountByCode(String code);

  Discount createDiscount(DiscountRequest request);

  Discount editDiscount(Map<String, Object> updates, Long id);

  void removeDiscount(Long id);

}
