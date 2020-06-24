package com.crm.payload.response;

import java.io.Serializable;
import java.util.List;

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

  private int pageNumber;
  
  private int pageSize;
  
  private long totalElements;
  
  private int totalPages;
  
  private List<E> contents;

}
