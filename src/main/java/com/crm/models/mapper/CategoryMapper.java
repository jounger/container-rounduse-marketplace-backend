package com.crm.models.mapper;

import com.crm.models.Category;
import com.crm.models.dto.CategoryDto;

public class CategoryMapper {
  public static CategoryDto toCategoryDto(Category category) {
    CategoryDto categoryDto = new CategoryDto();
    categoryDto.setId(category.getId());
    categoryDto.setName(category.getName());
    categoryDto.setDescription(category.getDescription());
    
    return categoryDto;    
  }
}
