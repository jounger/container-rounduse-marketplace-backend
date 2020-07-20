package com.crm.models.mapper;

import com.crm.models.Payment;
import com.crm.models.dto.PaymentDto;

public class PaymentMapper {

  public static PaymentDto toPaymentDto(Payment payment) {
    PaymentDto paymentDto = new PaymentDto();
    
    paymentDto.setId(payment.getId());
    
    String sender = payment.getSender().getUsername();
    paymentDto.setSender(sender);
    
    String recipient = payment.getRecipient().getUsername();
    paymentDto.setRecipient(recipient);
    
    paymentDto.setContract(payment.getContract().getId());
    
    String detail = payment.getDetail();
    paymentDto.setDetail(detail);
    
    Double amount = payment.getAmount();
    paymentDto.setAmount(amount);
    
    Boolean isPaid = payment.getIsPaid();
    paymentDto.setIsPaid(isPaid);
    
    String type = payment.getType();
    paymentDto.setType(type);
    
    String paymentDate = payment.getPaymentDate().toString();
    paymentDto.setPaymentDate(paymentDate);
    
    return paymentDto;
    
  }
}
