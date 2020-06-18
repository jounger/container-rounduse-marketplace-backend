package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
  
  Boolean existsByName(String name);
  
  Optional<Category> findByName(String name);
}
