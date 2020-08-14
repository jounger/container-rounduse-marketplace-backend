package com.crm.services.impl;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorMessage;
import com.crm.enums.EnumFileType;
import com.crm.exception.NotFoundException;
import com.crm.models.FileUpload;
import com.crm.models.User;
import com.crm.payload.request.FileUploadRequest;
import com.crm.repository.FileUploadRepository;
import com.crm.repository.UserRepository;
import com.crm.services.FileStorageService;
import com.crm.services.FileUploadService;

@Service
public class FileUploadServiceImpl implements FileUploadService {

  @Autowired
  private FileStorageService fileStorageService;

  @Autowired
  private FileUploadRepository fileUploadRepository;

  @Autowired
  private UserRepository userRepository;

  @Override
  public FileUpload createFileUpload(String username, FileUploadRequest request) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String salt = String.valueOf(timestamp.getTime());
    String fileName = fileStorageService.storeFile(salt, request.getFile());
//  String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/file/download/")
//      .path(fileName).toUriString();

    FileUpload fileUpload = new FileUpload();
    fileUpload.setOwner(user);
    fileUpload.setName(fileName);
    fileUpload.setOriginName(request.getFile().getOriginalFilename());
    fileUpload.setPath("/api/file/download/");

    EnumFileType type = EnumFileType.findByName(request.getType());
    if (type != null)
      fileUpload.setType(type.name());
    else
      fileUpload.setType(EnumFileType.IMAGE.name());

    FileUpload _fileUpload = fileUploadRepository.save(fileUpload);
    return _fileUpload;
  }

  @Override
  public FileUpload getFileUpload(String name) {
    FileUpload fileUpload = fileUploadRepository.findByName(name)
        .orElseThrow(() -> new NotFoundException("File's not found"));
    return fileUpload;
  }

}
