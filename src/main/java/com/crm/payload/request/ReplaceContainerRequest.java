package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplaceContainerRequest {

  private Long oldContainerId;
  
  private Long newContainerId;
}
