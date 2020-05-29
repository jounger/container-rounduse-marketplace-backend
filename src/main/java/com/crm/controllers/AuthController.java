package com.crm.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  
  @GetMapping("/login")
  public String authenticateUser() {
    return "Hello my friend";
  }
  
}
