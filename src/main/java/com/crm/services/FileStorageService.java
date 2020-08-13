package com.crm.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

  String storeFile(MultipartFile file);

  String storeFile(String salt, MultipartFile file);

  Resource loadFileAsResource(String fileName);
}
