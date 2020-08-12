package com.crm.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.FileUpload;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SignUpRequest;

public interface UserService {

  Page<User> getUsers(PaginationRequest request);

  Page<User> searchUsers(PaginationRequest request, String search);

  List<User> getUsersByRole(String roleName);

  User createUser(SignUpRequest request);

  User editProfileImage(String username, FileUpload profileImage);

  User editUser(Long id, Map<String, Object> updates);
}
