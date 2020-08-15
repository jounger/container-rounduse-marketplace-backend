package com.crm.models.mapper;

import com.crm.models.Payment;
import com.crm.models.dto.PaymentDto;

public class PaymentMapper {

  public static PaymentDto toPaymentDto(Payment payment) {
    if (payment == null) {
      return null;
    }

    PaymentDto paymentDto = new PaymentDto();

    paymentDto.setId(payment.getId());

    paymentDto.setSender(SupplierMapper.toSupplierDto(payment.getSender()));

    paymentDto.setRecipient(SupplierMapper.toSupplierDto(payment.getRecipient()));

    paymentDto.setContract(ContractMapper.toContractDto(payment.getContract()));

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
