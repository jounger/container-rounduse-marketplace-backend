package com.crm.payload.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileUploadRequest {

  private String name;
  
  private String type;

  private MultipartFile file;

}
