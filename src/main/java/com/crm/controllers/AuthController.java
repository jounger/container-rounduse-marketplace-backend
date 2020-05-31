package com.crm.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.payload.request.SignInRequest;
import com.crm.payload.request.SignUpRequest;
import com.crm.payload.response.JwtResponse;
import com.crm.payload.response.MessageResponse;
import com.crm.security.jwt.JwtUntils;
import com.crm.security.services.UserDetailsImpl;
import com.crm.services.UserService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  
  @Autowired
  private AuthenticationManager authenticationManager;
  
  @Autowired
  private UserService userService;

  @Autowired
  private JwtUntils jwtUntils;
  
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest request) {
    logger.info(request.getUsername());
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUntils.generateJwtToken(authentication);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
        .map(role -> role.getAuthority())
        .collect(Collectors.toList());
    logger.info("JWT: {}", jwt);
    JwtResponse response = new JwtResponse();
    response.setToken(jwt);
    response.setId(userDetails.getId());
    response.setUsername(userDetails.getUsername());
    response.setFullname(userDetails.getFullname());
    response.setEmail(userDetails.getEmail());
    response.setRoles(roles);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Access-Control-Expose-Headers", "Authorization");
    responseHeaders.set("Authorization", "Bearer " + jwt);
    return ResponseEntity.ok().headers(responseHeaders).body(response);
  }
  
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest request) {
    
    userService.saveUser(request);
    
    return ResponseEntity.ok(new MessageResponse("User registered succesfully"));
  }
  
}
