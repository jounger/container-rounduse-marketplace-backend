package com.crm.models.dto;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IcdDto {
  private Long id;

  private String fullname;

  private String nameCode;

  private String address;

  private Collection<String> shippingLines;
}