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

import com.crm.payload.request.SystemAdminRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.services.AdminSystemService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	@Autowired
	private AdminSystemService adminSystemService;
	
	@PostMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> createAdmin(@Valid @RequestBody SystemAdminRequest request){	
		adminSystemService.saveAdmin(request);
		return ResponseEntity.ok(new MessageResponse("Admin created successfully"));
	}
}
