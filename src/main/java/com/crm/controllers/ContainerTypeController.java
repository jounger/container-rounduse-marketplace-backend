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

import com.crm.payload.request.ContainerTypeRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.services.ContainerTypeService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/containertype")
public class ContainerTypeController {

	@Autowired
	private ContainerTypeService containerTypeService;
	
	@PostMapping("containertype")
	@PreAuthorize("hasRole('OPERATOR')")
	public ResponseEntity<?> createContainerType(@Valid @RequestBody ContainerTypeRequest request){
		containerTypeService.saveContainerType(request);
		return ResponseEntity.ok(new MessageResponse("Container Type created successfully"));
	}
}
