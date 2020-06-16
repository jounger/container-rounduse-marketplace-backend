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

import com.crm.payload.request.PortRequest;
import com.crm.services.PortService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/port")
public class PortController {

	@Autowired
	private PortService portService;
	
	@PostMapping("/")
	@PreAuthorize("hasRole('OPERATOR')")
	public ResponseEntity<?> createPort(@Valid @RequestBody PortRequest request){
		portService.savePort(request);
		return ResponseEntity.ok("Port created successfully");
	}
}
