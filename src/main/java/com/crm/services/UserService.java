package com.crm.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.data.domain.Page;

import com.crm.models.User;
import com.crm.payload.request.ChangePasswordRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SignUpRequest;

public interface UserService {

  User createUser(SignUpRequest request);

  Page<User> getUsers(PaginationRequest request);

  Page<User> searchUsers(PaginationRequest request, String search);

  User changeStatus(Long id, Map<String, Object> updates);

  List<User> getUsersByRole(String roleName);

  User changePassword(String username, ChangePasswordRequest request);

  void getResetPasswordToken(String email) throws MessagingException, IOException;

  Boolean isValidResetPasswrodTolken(String token);

  void resetPasswordByToken(String token, String newPassword);

}
