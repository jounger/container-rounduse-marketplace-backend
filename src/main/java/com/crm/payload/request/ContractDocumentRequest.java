package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ContractDocumentRequest {

  private Long id;

  private String sender;

  private String documentPath;

  private String status;
}
