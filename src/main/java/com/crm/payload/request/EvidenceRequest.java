package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EvidenceRequest {

  private Long id;

  private String sender;

  private String documentPath;

  private Boolean isValid;
}
