package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Supplier;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SupplierRequest;

public interface SupplierService {

	Page<Supplier> getSuppliers(PaginationRequest request);
	
	Supplier getSupplier(String username);
	
	Page<Supplier> getSuppliersByStatus(PaginationRequest request);
	
	Supplier editSupplierStatus(SupplierRequest request);
	
}
