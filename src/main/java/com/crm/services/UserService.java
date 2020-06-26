package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SignUpRequest;

public interface UserService {
  
  void createUser(SignUpRequest request);

  Page<User> getUsers(PaginationRequest request);

  User changeStatus(Long id, Map<String, Object> updates);
  
}
