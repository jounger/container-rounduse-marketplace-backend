package com.crm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.services.ForwarderService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/forwarder")
public class ForwarderController {
	
	private static final Logger logger = LoggerFactory.getLogger(ForwarderController.class);
	
	@Autowired
	private ForwarderService forwarderService;
	
}
