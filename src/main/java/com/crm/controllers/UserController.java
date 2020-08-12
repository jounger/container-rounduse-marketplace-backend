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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.crm.models.User;
import com.crm.models.dto.UserDto;
import com.crm.models.mapper.UserMapper;
import com.crm.payload.request.FileUploadRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.PaginationResponse;
import com.crm.payload.response.UploadFileResponse;
import com.crm.services.FileStorageService;
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
  private FileStorageService fileStorageService;

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
    User user = userService.editUser(id, updates);
    UserDto userDto = UserMapper.toUserDto(user);
    return ResponseEntity.ok(userDto);
  }

  @Transactional
  @PreAuthorize("hasRole('MODERATOR') or hasRole('MERCHANT') or hasRole('FORWARDER')")
  @PostMapping("/upload")
  public ResponseEntity<?> uploadProfileImage(@RequestBody FileUploadRequest request) {
    String fileName = fileStorageService.storeFile(request.getFile());
    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/file/download/")
        .path(fileName).toUriString();

    if (fileName != null) {
      fileUploadService.createFileUpload(request);
    }

    UploadFileResponse uploadFileResponse = new UploadFileResponse();
    uploadFileResponse.setFileName(fileName);
    uploadFileResponse.setFileDownloadUri(fileDownloadUri);
    uploadFileResponse.setFileType(request.getFile().getContentType());
    uploadFileResponse.setSize(request.getFile().getSize());

    return ResponseEntity.status(HttpStatus.CREATED).body(uploadFileResponse);
  }
}
