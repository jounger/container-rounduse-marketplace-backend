package com.crm.payload.response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponse<E> implements Serializable{

  private static final long serialVersionUID = 1L;

  @JsonProperty("page")
  private int pageNumber;
  
  @JsonProperty("limit")
  private int pageSize;
  
  @JsonProperty("totalElements")
  private long totalElements;
  
  @JsonProperty("totalPages")
  private int totalPages;
  
  @JsonProperty("data")
  private List<E> contents;

}
