package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.Payment;
import com.crm.specification.PaymentSpecification;

public class PaymentSpecificationsBuilder extends GeneticSpecificationsBuilder<Payment>{

  @Override
  public Specification<Payment> build() {
    if (super.params.size() == 0) {
      return null;
    }
    PaymentSpecification paymentSpecification = new PaymentSpecification();
    paymentSpecification.setCriteria(super.params.get(0));
    Specification<Payment> result = paymentSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      PaymentSpecification _paymentSpecification = new PaymentSpecification();
      _paymentSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_paymentSpecification);
    }
    return result;
  }

}
