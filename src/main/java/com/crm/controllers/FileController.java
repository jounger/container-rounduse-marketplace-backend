package com.crm.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.payload.request.UploadFile;

@RestController
public class FileController {

  private static String UPLOAD_DIR = System.getProperty("user.home") + "/upload";

  @GetMapping("/file/{filename}")
  public ResponseEntity<?> downloadFile(@PathVariable String filename) {
    File file = new File(UPLOAD_DIR + "/" + filename);
    if (!file.exists()) {
      throw new NotFoundException("File is not found");
    }
    UrlResource resource;
    try {
      resource = new UrlResource(file.toURI());
    } catch (MalformedURLException e) {
      throw new NotFoundException("File is not found");
    }
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"").body(resource);
  }

  @PostMapping("/upload")
  public ResponseEntity<?> uploadFile(@ModelAttribute("uploadForm") UploadFile form) {

    // create folder to save file if not exist
    File uploadDir = new File(UPLOAD_DIR);
    if (!uploadDir.exists()) {
      uploadDir.mkdirs();
    }
    MultipartFile fileData = form.getFileData();
    String name = fileData.getOriginalFilename();
    if (name != null && name.length() > 0) {
      try {
        // create file
        File serverFile = new File(UPLOAD_DIR + "/" + name);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        stream.write(fileData.getBytes());
        stream.close();
        return ResponseEntity.ok("/file/" + name);
      } catch (Exception e) {
        throw new InternalException("Error when uploading");
      }
    }

    return ResponseEntity.badRequest().body("Bad request");
  }

}
