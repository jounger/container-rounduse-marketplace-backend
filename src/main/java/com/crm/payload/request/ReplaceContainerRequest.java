package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReplaceContainerRequest {

  private Long oldContainerId;
  
  private Long newContainerId;
}
