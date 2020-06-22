package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Category;
import com.crm.payload.request.CategoryRequest;
import com.crm.payload.request.PaginationRequest;

public interface CategoryService {
  
  Page<Category> getCategories(PaginationRequest request);

  Category getCategoryById(Long id);
  
  void createCategory(CategoryRequest request); 

  Category updateCategory(CategoryRequest request);

  void removeCategory(Long id);
  
}
