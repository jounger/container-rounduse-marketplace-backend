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

import com.crm.payload.request.MerchantRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.services.MerchantService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/merchant")
public class MerchantController {
	
	@Autowired
	private MerchantService merchantService;
	
	@PostMapping("/")
	public ResponseEntity<?> saveMerchant(@Valid @RequestBody MerchantRequest request){
		merchantService.saveMerchant(request);
		return ResponseEntity.ok(new MessageResponse("Merchant created sucssessfully."));
	}
}
