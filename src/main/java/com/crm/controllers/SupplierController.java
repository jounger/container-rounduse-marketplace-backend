package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Supplier;
import com.crm.models.dto.SupplierDto;
import com.crm.models.mapper.SupplierMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SupplierRequest;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.SupplierService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

	private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);
	
	@Autowired
	private SupplierService supplierService;
	
	@PreAuthorize("hasRole('OPERATOR')")
	@GetMapping("")
	public ResponseEntity<?> getSuppliers(@Valid @RequestBody PaginationRequest request) {
		logger.info("Page request: {}", request.getPage());
		
		Page<Supplier> pages = null;
		if(request.getStatus() == null) {
		  pages = supplierService.getSuppliers(request);
		}else if(request.getStatus() != null) {
		  pages = supplierService.getSuppliersByStatus(request);
		}	
		
		PaginationResponse<SupplierDto> response = new PaginationResponse<>();
		response.setPageNumber(request.getPage());
		response.setPageSize(request.getLimit());
		response.setTotalElements(pages.getTotalElements());
		response.setTotalPages(pages.getTotalPages());

		List<Supplier> suppliers = pages.getContent();
		List<SupplierDto> suppliersDto = new ArrayList<>();
		suppliers.forEach(supplier -> suppliersDto.add(SupplierMapper.toSupplierDto(supplier)));
		response.setContents(suppliersDto);

		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("hasRole('OPERATOR') or hasRole('FORWARDER') or hasRole('MERCHANT')")
    @GetMapping("registration")
    public ResponseEntity<?> getSupplier(@Valid @RequestBody SupplierRequest request) {
        Supplier supplier = supplierService.getSupplier(request.getUsername());
        SupplierDto supplierDto = SupplierMapper.toSupplierDto(supplier);
        return ResponseEntity.ok(supplierDto);
    }
	
	@PreAuthorize("hasRole('OPERATOR')")
    @GetMapping("/edit")
    public ResponseEntity<?> editSupplierStatus(@Valid @RequestBody SupplierRequest request) {
        Supplier supplier = supplierService.editSupplierStatus(request);
        SupplierDto supplierDto = SupplierMapper.toSupplierDto(supplier);
        return ResponseEntity.ok(supplierDto);
    }
}
