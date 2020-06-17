   package com.crm.services;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crm.models.User;
import com.crm.payload.request.ChangeUserStatusRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SignUpRequest;

public interface UserService {

  void saveUser(SignUpRequest request);

  Page<User> getUsers(PaginationRequest request);

  void changeStatus(ChangeUserStatusRequest request);

}
