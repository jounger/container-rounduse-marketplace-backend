package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Category;
import com.crm.payload.request.CategoryRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.CategoryRepository;
import com.crm.services.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  private CategoryRepository categoryRepository;

  @Override
  public Page<Category> getCategories(PaginationRequest request) {
    Page<Category> pages = categoryRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return pages;
  }

  @Override
  public Category getCategoryById(Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Category is not found."));
    return category;
  }

  @Override
  public void createCategory(CategoryRequest request) {
    Category category = new Category();
    if (categoryRepository.existsByName(request.getName())) {
      throw new DuplicateRecordException("Error: Category has been existed");
    }
    category.setName(request.getName());
    category.setDescription(request.getDescription());
    categoryRepository.save(category);
  }

  @Override
  public Category updateCategory(CategoryRequest request) {
    Category category = categoryRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("ERROR: Category is not found."));
    category.setName(request.getName());
    category.setDescription(request.getDescription());
    categoryRepository.save(category);
    return category;
  }

  @Override
  public void removeCategory(Long id) {
    if (categoryRepository.existsById(id)) {
      categoryRepository.deleteById(id);
    } else {
      throw new NotFoundException("Error: Category is not found.");
    }
  }

}
