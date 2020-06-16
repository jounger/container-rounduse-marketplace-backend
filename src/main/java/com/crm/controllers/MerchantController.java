package com.crm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.services.MerchantService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/merchant")
public class MerchantController {
	
	@Autowired
	private MerchantService merchantService;
	
	
}
