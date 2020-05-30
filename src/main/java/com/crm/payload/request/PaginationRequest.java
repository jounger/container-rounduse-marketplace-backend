package com.crm.payload.request;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

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
  @JsonProperty("page")
  private int pageNumber;
  
  @NotBlank
  @JsonProperty("limit")
  private int pageSize;
  
}
