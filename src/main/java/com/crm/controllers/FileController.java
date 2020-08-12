package com.crm.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.crm.payload.response.UploadFileResponse;
import com.crm.services.FileStorageService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/file")
public class FileController {

  private static final Logger logger = LoggerFactory.getLogger(FileController.class);

  @Autowired
  private FileStorageService fileStorageService;

  @PreAuthorize("hasRole('MODERATOR') or hasRole('MERCHANT') or hasRole('FORWARDER')")
  @PostMapping("/upload")
  public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
    String fileName = fileStorageService.storeFile(file);

    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/file/download/").path(fileName)
        .toUriString();

    UploadFileResponse uploadFileResponse = new UploadFileResponse();
    uploadFileResponse.setFileName(fileName);
    uploadFileResponse.setFileDownloadUri(fileDownloadUri);
    uploadFileResponse.setFileType(file.getContentType());
    uploadFileResponse.setSize(file.getSize());

    return ResponseEntity.status(HttpStatus.CREATED).body(uploadFileResponse);
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('MERCHANT') or hasRole('FORWARDER')")
  @PostMapping("/uploadMultiple")
  public ResponseEntity<?> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
    List<UploadFileResponse> uploadFileResponses = new ArrayList<>();
    Arrays.asList(files).forEach(file -> {
      String fileName = fileStorageService.storeFile(file);

      String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/file/download/")
          .path(fileName).toUriString();

      UploadFileResponse uploadFileResponse = new UploadFileResponse();
      uploadFileResponse.setFileName(fileName);
      uploadFileResponse.setFileDownloadUri(fileDownloadUri);
      uploadFileResponse.setFileType(file.getContentType());
      uploadFileResponse.setSize(file.getSize());

      uploadFileResponses.add(uploadFileResponse);
    });
    return ResponseEntity.status(HttpStatus.CREATED).body(uploadFileResponses);
  }

//  @PreAuthorize("hasRole('MODERATOR') or hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/download/{fileName:.+}")
  public ResponseEntity<?> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
    // Load file as Resource
    Resource resource = fileStorageService.loadFileAsResource(fileName);

    // Try to determine file's content type
    String contentType = null;
    try {
      contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
    } catch (IOException ex) {
      logger.info("Could not determine file type.");
    }

    // Fallback to the default content type if type could not be determined
    if (contentType == null) {
      contentType = "application/octet-stream";
    }

    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);
  }
}
