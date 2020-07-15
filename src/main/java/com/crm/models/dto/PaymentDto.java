package com.crm.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

  private Long id;

  private String sender;

  private String recipient;

  private Long contract;

  private String detail;

  private Double amount;

  private Boolean isPaid;

  private String type;

  private String paymentDate;
}
