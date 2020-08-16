package com.crm.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crm.common.SuccessMessage;
import com.crm.enums.EnumFileType;
import com.crm.models.FileUpload;
import com.crm.models.User;
import com.crm.models.dto.UserDto;
import com.crm.models.mapper.UserMapper;
import com.crm.payload.request.ChangePasswordRequest;
import com.crm.payload.request.FileUploadRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ResetPasswordRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.FileUploadService;
import com.crm.services.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;

  @Autowired
  private FileUploadService fileUploadService;

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
  @RequestMapping(value = "/{username}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editUser(@PathVariable("username") String username,
      @RequestBody Map<String, Object> updates) {

    User user = userService.editUser(username, updates);
    UserDto userDto = UserMapper.toUserDto(user);

    // Set default response body
    DefaultResponse<UserDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_USER_SUCCESSFULLY);
    defaultResponse.setData(userDto);
    logger.info("Moderator editUser {} with request {}", username, updates.toString());

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
    logger.info("{} changePassword with request {}", username, request.toString());

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PostMapping("/upload-profile")
  public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    FileUploadRequest request = new FileUploadRequest();
    request.setFile(file);
    request.setType(EnumFileType.IMAGE.name());

    FileUpload fileUpload = fileUploadService.createFileUpload(username, request);
    String filePath = fileUpload.getPath() + fileUpload.getName();

    Map<String, Object> updates = new HashMap<>();
    updates.put("profileImagePath", filePath);
    User user = userService.editUser(username, updates);
    UserDto userDto = UserMapper.toUserDto(user);

    DefaultResponse<UserDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage("Update profile successful");
    defaultResponse.setData(userDto);
    logger.info("{} do uploadProfileImage with path: {}", username, filePath);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @PostMapping("/reset-password")
  public ResponseEntity<?> getResetPasswordToken(@Valid @RequestBody ResetPasswordRequest request)
      throws MessagingException, IOException {
    userService.getResetPasswordToken(request.getEmail());
    DefaultResponse<UserDto> response = new DefaultResponse<UserDto>();
    response.setMessage(SuccessMessage.GENERATE_RESET_PASSWORD_TOKEN_SUCCESSFULLY);
    logger.info("Reset password with email: {}", request.getEmail());
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/reset-password")
  public ResponseEntity<?> isValidResetPasswordToken(@Valid @RequestBody ResetPasswordRequest request) {
    Boolean isValidResetPasswordToken = userService.isValidResetPasswrodTolken(request.getToken());
    DefaultResponse<Boolean> response = new DefaultResponse<Boolean>();
    response.setData(isValidResetPasswordToken);
    logger.info("Reset Password Token: {}", request.getToken());
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PatchMapping("/reset-password")
  public ResponseEntity<?> resetPasswordByToken(HttpServletRequest httpRequest,
      @Valid @RequestBody ResetPasswordRequest request) {
    String token = httpRequest.getHeader("Authentication");
    userService.resetPasswordByToken(token, request.getNewPassword());
    DefaultResponse<Boolean> response = new DefaultResponse<Boolean>();
    response.setMessage(SuccessMessage.CHANGE_PASSWORD_SUCCESSFULLY);
    logger.info("Reset Password Token: {}", token);
    logger.info("Reset Password Token: {}", request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
