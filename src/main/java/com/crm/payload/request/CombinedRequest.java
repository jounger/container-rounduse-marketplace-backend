package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CombinedRequest {

  private Long id;

  private Boolean isCanceled;

  private ContractRequest contract;
}
