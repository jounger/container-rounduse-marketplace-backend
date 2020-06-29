package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Supplier;
import com.crm.payload.request.PaginationRequest;

public interface SupplierService {

	Page<Supplier> getSuppliers(PaginationRequest request);
	
	Supplier getSupplier(String username);
	
	Supplier getSupplier(Long id);
	
	Page<Supplier> getSuppliersByStatus(PaginationRequest request);
	
	Supplier editSupplier(Map<String, Object> updates, Long id);
	
}
