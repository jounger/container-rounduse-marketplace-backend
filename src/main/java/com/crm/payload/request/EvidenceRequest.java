package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvidenceRequest {

  private Long id;
  
  private String sender;
  
  private FileUploadRequest document;
  
  private Boolean isValid;
}
