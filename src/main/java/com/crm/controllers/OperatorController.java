package com.crm.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.payload.request.OperatorRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.services.OperatorService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/operator")
public class OperatorController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@Autowired
	private OperatorService operatorService;
	
	@PostMapping("/")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> createOperator(@Valid @RequestBody OperatorRequest request){		
		operatorService.saveOperator(request);
		logger.info("Operator {} created.", request.getUsername());
		return ResponseEntity.ok(new MessageResponse("Operator created successfully"));		
	}
	
}
