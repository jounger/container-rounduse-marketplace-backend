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

import com.crm.payload.request.DriverRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.services.DriverService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/driver")
public class DriverController {

private static final Logger logger = LoggerFactory.getLogger(DriverController.class);
	
	@Autowired
	private DriverService driverService;
	
	@PostMapping("")
	@PreAuthorize("hasRole('FORWARDER')")
	public ResponseEntity<?> createDriver(@Valid @RequestBody DriverRequest request){		
		logger.info("Driver request: {}", request);
		driverService.saveDriver(request);
		
		return ResponseEntity.ok(new MessageResponse("Driver created successfully"));
	}
}
