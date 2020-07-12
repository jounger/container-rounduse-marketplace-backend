package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
  
  private Long id;
  
  private String sender;
  
  private String recipient;

  private String detail;
  
  private Double amount;
  
  private Boolean isPaid;
  
  private String type;
  
  private String paymentDate;
}
