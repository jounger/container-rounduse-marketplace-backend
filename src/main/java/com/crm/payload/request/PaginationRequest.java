package com.crm.payload.request;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull
  private Integer page;

  @NotNull
  private Integer limit;
  
  private String status;
  
}
