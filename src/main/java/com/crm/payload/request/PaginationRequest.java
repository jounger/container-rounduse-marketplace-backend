package com.crm.payload.request;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotBlank
  private int page;

  @NotBlank
  private int limit;
  
  private String status;
  
}
