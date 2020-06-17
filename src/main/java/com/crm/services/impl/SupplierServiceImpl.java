package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.crm.exception.NotFoundException;
import com.crm.models.Supplier;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.SupplierRepository;
import com.crm.services.SupplierService;

@Service
public class SupplierServiceImpl implements SupplierService{

	@Autowired
	private SupplierRepository supplierRepository;
	
	@Override
	public Page<Supplier> getSuppliers(PaginationRequest request) {
		Page<Supplier> pages = supplierRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
		return pages;
	}

	@Override
	public Supplier getSupplier(String username) {
		Supplier supplier = supplierRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("User is not found"));
		return supplier;
	}

}
