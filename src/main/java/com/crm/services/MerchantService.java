package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Merchant;
import com.crm.payload.request.MerchantRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SupplierRequest;

public interface MerchantService {

  void createMerchant(SupplierRequest request);

  Merchant getMerchant(Long id);

  Page<Merchant> getMerchants(PaginationRequest request);

  Merchant updateMerchant(MerchantRequest request);

  Merchant editMerchant(Long id, Map<String, Object> updates);

  void removeMerchant(Long id);
}
