package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.User;
import com.crm.models.dto.UserDto;
import com.crm.models.mapper.UserMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.UserService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/admin")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);
  
  @Autowired
  private UserService userService;
  
//  @GetMapping("/user")
//  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
//  public ResponseEntity<?> getUsers(@Valid PaginationRequest request) {
//    logger.info("Page request: {}", request.getPage());
//    Page<User> pages = userService.getUsers(request);
//    PaginationResponse<UserDto> response = new PaginationResponse<>();
//    response.setPageNumber(request.getPage());
//    response.setPageSize(request.getLimit());
//    response.setTotalElements(pages.getTotalElements());
//    response.setTotalPages(pages.getTotalPages());
//    
//    List<User> users = pages.getContent();
//    List<UserDto> usersDto = new ArrayList<>();
//    users.forEach(user -> usersDto.add(UserMapper.toUserDto(user)));
//    response.setContents(usersDto);
//    
//    return ResponseEntity.ok(response);
//  }
}
