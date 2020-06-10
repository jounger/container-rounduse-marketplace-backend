   package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SignUpRequest;

public interface UserService {
  
  void saveUser(SignUpRequest request);
  
  Page<User> getUsers(PaginationRequest request);
  
}
