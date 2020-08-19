package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.Invoice;
import com.crm.specification.InvoiceSpecification;

public class InvoiceSpecificationsBuilder extends GeneticSpecificationsBuilder<Invoice>{

  @Override
  public Specification<Invoice> build() {
    if (super.params.size() == 0) {
      return null;
    }
    InvoiceSpecification invoiceSpecification = new InvoiceSpecification();
    invoiceSpecification.setCriteria(super.params.get(0));
    Specification<Invoice> result = invoiceSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      InvoiceSpecification _paymentSpecification = new InvoiceSpecification();
      _paymentSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_paymentSpecification);
    }
    return result;
  }

}
