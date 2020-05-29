package com.crm.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.payload.request.SignInRequest;
import com.crm.payload.request.SignUpRequest;
import com.crm.repository.RoleRepository;
import com.crm.repository.UserRepository;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  
  @Autowired
  AuthenticationManager authenticationManager;
  
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  RoleRepository roleRepository;
  
  @Autowired
  
  @GetMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest request) {
    return null;
  }
  
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest request) {
    
    return null;
  }
  
}
