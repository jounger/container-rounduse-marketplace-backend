package com.crm.payload.request;

import com.crm.enums.EnumSearchOperation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {

  private String key;
  private EnumSearchOperation operation;
  private Object value;
}