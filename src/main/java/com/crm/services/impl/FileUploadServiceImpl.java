package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.exception.NotFoundException;
import com.crm.models.FileUpload;
import com.crm.payload.request.FileUploadRequest;
import com.crm.repository.FileUploadRepository;
import com.crm.services.FileStorageService;
import com.crm.services.FileUploadService;

@Service
public class FileUploadServiceImpl implements FileUploadService {

  @Autowired
  private FileStorageService fileStorageService;

  @Autowired
  private FileUploadRepository fileUploadRepository;

  @Override
  public FileUpload createFileUpload(FileUploadRequest request) {
    String fileName = fileStorageService.storeFile(request.getFile());
//  String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/file/download/")
//      .path(fileName).toUriString();

    FileUpload fileUpload = new FileUpload();
    fileUpload.setName(request.getName());
    fileUpload.setOriginName(fileName);
    fileUpload.setPath("/api/file/download/");
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
