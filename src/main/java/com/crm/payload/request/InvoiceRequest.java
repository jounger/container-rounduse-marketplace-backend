package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InvoiceRequest {
  
  private Long id;

  private String sender;

  private String recipient;

  private String detail;

  private Double amount;

  private Boolean isPaid;

  private String type;

  private String paymentDate;
}
