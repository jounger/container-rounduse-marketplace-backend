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

import com.crm.payload.request.IcdRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.services.IcdService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/icd")
public class IcdController {

	@Autowired
	private IcdService icdService;
	
	@PostMapping("/")
	@PreAuthorize("hasRole('OPERATOR')")
	public ResponseEntity<?> createIcd(@Valid @RequestBody IcdRequest request){
		icdService.saveIcd(request);
		return ResponseEntity.ok(new MessageResponse("ICD created successfully"));
	}
	
}
