package com.crm.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceDto {

  private Long Id;

  private SupplierDto sender;

  private String documentPath;

  private String status;

}
