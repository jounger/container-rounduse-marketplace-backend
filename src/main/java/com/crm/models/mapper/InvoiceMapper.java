package com.crm.models.mapper;

import com.crm.models.Invoice;
import com.crm.models.dto.InvoiceDto;

public class InvoiceMapper {

  public static InvoiceDto toPaymentDto(Invoice invoice) {
    if (invoice == null) {
      return null;
    }

    InvoiceDto invoiceDto = new InvoiceDto();

    invoiceDto.setId(invoice.getId());

    invoiceDto.setSender(SupplierMapper.toSupplierDto(invoice.getSender()));

    invoiceDto.setRecipient(SupplierMapper.toSupplierDto(invoice.getRecipient()));

    invoiceDto.setContract(ContractMapper.toContractDto(invoice.getContract()));

    String detail = invoice.getDetail();
    invoiceDto.setDetail(detail);

    Double amount = invoice.getAmount();
    invoiceDto.setAmount(amount);

    Boolean isPaid = invoice.getIsPaid();
    invoiceDto.setIsPaid(isPaid);

    String type = invoice.getType();
    invoiceDto.setType(type);

    String paymentDate = invoice.getPaymentDate().toString();
    invoiceDto.setPaymentDate(paymentDate);

    return invoiceDto;

  }
}
