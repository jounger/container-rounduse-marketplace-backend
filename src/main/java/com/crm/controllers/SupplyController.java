package com.crm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.payload.response.DefaultResponse;
import com.crm.services.SupplyService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/supply")
public class SupplyController {

  @Autowired
  private SupplyService supplyService;

  @GetMapping("/{code}")
  public ResponseEntity<?> existsByCode(@PathVariable("code") String code) {
    Boolean exist = supplyService.existsByCode(code);
    DefaultResponse<Boolean> response = new DefaultResponse<Boolean>();
    response.setData(exist);
    return ResponseEntity.ok(response);
  }

}
