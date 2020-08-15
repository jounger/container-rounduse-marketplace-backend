package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CombinedRequest {

  private Long id;

  private Boolean isCanceled;

  private ContractRequest contract;
}
