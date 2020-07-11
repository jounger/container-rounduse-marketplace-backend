package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvidenceRequest {

  private Long id;
  
  private String sender;
  
  private String evidence;
  
  private Boolean isValid;
}
