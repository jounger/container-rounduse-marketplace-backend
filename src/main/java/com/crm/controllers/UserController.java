package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.User;
import com.crm.models.dto.UserDto;
import com.crm.models.mapper.UserMapper;
import com.crm.payload.request.ChangePasswordRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;

  /*
   * REF:
   * https://www.baeldung.com/rest-api-search-language-spring-data-specifications
   * TODO: Spring Data JPA Specifications EXAMPLE:
   * http://localhost:8085/api/user/filter?page=0&limit=10&search=phone:0967390098
   * ,email~crm,status!active
   */
  @GetMapping("/filter")
  public ResponseEntity<?> searchUsers(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {
    logger.info("Page request: {}", request.getPage());
    logger.info("Search: {}", search);
    Page<User> pages = userService.searchUsers(request, search);
    PaginationResponse<UserDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<User> users = pages.getContent();
    List<UserDto> usersDto = new ArrayList<>();
    users.forEach(user -> usersDto.add(UserMapper.toUserDto(user)));
    response.setContents(usersDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("")
  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<?> getUsers(@Valid PaginationRequest request) {
    logger.info("Page request: {}", request.getPage());
    Page<User> pages = userService.getUsers(request);
    PaginationResponse<UserDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<User> users = pages.getContent();
    List<UserDto> usersDto = new ArrayList<>();
    users.forEach(user -> usersDto.add(UserMapper.toUserDto(user)));
    response.setContents(usersDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PreAuthorize("hasRole('MODERATOR')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> changeStatus(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    User user = userService.changeStatus(id, updates);
    UserDto userDto = UserMapper.toUserDto(user);

    // Set default response body
    DefaultResponse<UserDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_USER_SUCCESSFULLY);
    defaultResponse.setData(userDto);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @RequestMapping(value = "/change-password", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    User user = userService.changePassword(username, request);
    UserDto userDto = UserMapper.toUserDto(user);

    // Set default response body
    DefaultResponse<UserDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CHANGE_PASSWORD_SUCCESSFULLY);
    defaultResponse.setData(userDto);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
