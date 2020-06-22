package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Category;
import com.crm.models.dto.CategoryDto;
import com.crm.models.mapper.CategoryMapper;
import com.crm.payload.request.CategoryRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.CategoryService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/category")
public class CategoryController {

  @Autowired
  private CategoryService categoryService;

  @GetMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> getCategories(@Valid PaginationRequest request) {

    Page<Category> pages = categoryService.getCategories(request);
    PaginationResponse<CategoryDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Category> categories = pages.getContent();
    List<CategoryDto> categoriesDto = new ArrayList<>();
    categories.forEach(category -> categoriesDto.add(CategoryMapper.toCategoryDto(category)));
    response.setContents(categoriesDto);

    return ResponseEntity.ok(response);

  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> getCategory(@PathVariable Long id) {

    Category category = categoryService.getCategoryById(id);
    CategoryDto categoriesDto = new CategoryDto();
    categoriesDto = CategoryMapper.toCategoryDto(category);
    return ResponseEntity.ok(categoriesDto);
  }

  @PostMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest request) {
    categoryService.createCategory(request);
    return ResponseEntity.ok(new MessageResponse("Category created successfully"));
  }
  
  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> updateCategory(@Valid @RequestBody CategoryRequest request) {
    Category category = categoryService.updateCategory(request);
    CategoryDto categoriesDto = new CategoryDto();
    categoriesDto = CategoryMapper.toCategoryDto(category);
    return ResponseEntity.ok(categoriesDto);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> removeCategory(@PathVariable Long id) {
    categoryService.removeCategory(id);
    return ResponseEntity.ok(new MessageResponse("Category has remove successfully"));
  }
}
