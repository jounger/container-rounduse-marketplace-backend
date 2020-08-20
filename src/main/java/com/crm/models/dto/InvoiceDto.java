package com.crm.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {

  private Long id;

  private SupplierDto sender;

  private SupplierDto recipient;

  private ContractDto contract;

  private String detail;

  private Double amount;

  private Boolean isPaid;

  private String type;

  private String paymentDate;
}
