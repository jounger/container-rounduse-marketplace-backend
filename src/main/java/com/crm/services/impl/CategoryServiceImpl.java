package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.exception.DuplicateRecordException;
import com.crm.models.Category;
import com.crm.payload.request.CategoryRequest;
import com.crm.repository.CategoryRepository;
import com.crm.services.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  private CategoryRepository categoryRepository;

  @Override
  public void saveCategory(CategoryRequest request) {
    Category category = new Category();
    if (categoryRepository.existsByName(request.getName())) {
      throw new DuplicateRecordException("Error: Category has been existed");
    }
    category.setName(request.getName());
    category.setDesciption(request.getDesciption());
    categoryRepository.save(category);
  }

}
