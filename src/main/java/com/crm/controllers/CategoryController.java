package com.crm.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.payload.request.CategoryRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.services.CategoryService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/category")
public class CategoryController {
  
  @Autowired
  private CategoryService categoryService;
  
  @PostMapping("/")
  public ResponseEntity<?> createConsignment(@Valid @RequestBody CategoryRequest request){       
    categoryService.saveCategory(request);
    return ResponseEntity.ok(new MessageResponse("Category created successfully"));
  }
}
