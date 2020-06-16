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

import com.crm.payload.request.ForwarderRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.services.ForwarderService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/forwarder")
@PreAuthorize("hasRole('FORWARDER')")
public class ForwarderController {
	
	private static final Logger logger = LoggerFactory.getLogger(ForwarderController.class);
	
	@Autowired
	private ForwarderService forwarderService;
	

	
	@PostMapping("/container")
	public ResponseEntity<?> createForwarder(@Valid @RequestBody ForwarderRequest request){		
		forwarderService.saveForwarder(request);
		logger.info("Forwarder {} was created.", request.getUsername());
		return ResponseEntity.ok(new MessageResponse("Container created successfully"));
		
	}
}
