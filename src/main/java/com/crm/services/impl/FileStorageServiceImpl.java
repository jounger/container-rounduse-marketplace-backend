package com.crm.services.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.crm.common.FileUploadMessage;
import com.crm.exception.FileStorageException;
import com.crm.exception.MyFileNotFoundException;
import com.crm.services.FileStorageService;

@Service
public class FileStorageServiceImpl implements FileStorageService {

  @Value("${file.upload-dir}")
  private String UPLOAD_DIR;

  private Path fileStorageLocation;

  @Override
  public String storeFile(MultipartFile file) {
    // Normalize file name
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    this.fileStorageLocation = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();

    try {
      // Check if the file's name contains invalid characters
      if (fileName.contains("..")) {
        throw new FileStorageException(FileUploadMessage.FILE_UPLOAD_PATH_INVALID + fileName);
      }
      Files.createDirectories(this.fileStorageLocation);
      // Copy file to the target location (Replacing existing file with the same name)
      Path targetLocation = this.fileStorageLocation.resolve(fileName);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      return fileName;
    } catch (IOException ex) {
      throw new FileStorageException(FileUploadMessage.COULD_NOT_STORE_FILE + fileName, ex);
    }
  }

  @Override
  public String storeFile(String salt, MultipartFile file) {
    // Normalize file name
    String fileName = StringUtils.cleanPath(salt + "-" + file.getOriginalFilename());
    this.fileStorageLocation = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();

    try {
      // Check if the file's name contains invalid characters
      if (fileName.contains("..")) {
        throw new FileStorageException(FileUploadMessage.FILE_UPLOAD_PATH_INVALID + fileName);
      }
      Files.createDirectories(this.fileStorageLocation);
      // Copy file to the target location (Replacing existing file with the same name)
      Path targetLocation = this.fileStorageLocation.resolve(fileName);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      return fileName;
    } catch (IOException ex) {
      throw new FileStorageException(FileUploadMessage.COULD_NOT_STORE_FILE + fileName, ex);
    }
  }

  @Override
  public Resource loadFileAsResource(String fileName) {
    try {
      this.fileStorageLocation = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
      Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists()) {
        return resource;
      } else {
        throw new MyFileNotFoundException(FileUploadMessage.FILE_UPLOAD_NOTFOUND + fileName);
      }
    } catch (MalformedURLException ex) {
      throw new MyFileNotFoundException(FileUploadMessage.FILE_UPLOAD_NOTFOUND + fileName, ex);
    }
  }

}
