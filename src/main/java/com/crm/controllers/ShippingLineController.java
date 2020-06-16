package com.crm.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.payload.request.ShippingLineRequest;
import com.crm.services.ShippingLineService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/shippingline")
public class ShippingLineController {

	@Autowired
	private ShippingLineService shippingLineService;
	
	@PostMapping("/")
	@PreAuthorize("hasRole('OPERATOR')")
	public ResponseEntity<?> createShippingLine(@Valid @RequestBody ShippingLineRequest request){		
		shippingLineService.saveShippingLine(request);
		return ResponseEntity.ok("Shipping Line created successfully");
	}
}
