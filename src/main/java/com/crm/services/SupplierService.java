package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Supplier;
import com.crm.payload.request.PaginationRequest;

public interface SupplierService {

	Page<Supplier> getSuppliers(PaginationRequest request);
	
	Supplier getSupplier(String username);
	
}
