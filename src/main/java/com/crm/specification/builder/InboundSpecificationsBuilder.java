package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.Inbound;
import com.crm.specification.InboundSpecification;

public class InboundSpecificationsBuilder extends GeneticSpecificationsBuilder<Inbound> {

  @Override
  public Specification<Inbound> build() {
    if (super.params.size() == 0) {
      return null;
    }
    InboundSpecification inboundSpecification = new InboundSpecification();
    inboundSpecification.setCriteria(super.params.get(0));
    Specification<Inbound> result = inboundSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      InboundSpecification _inboundSpecification = new InboundSpecification();
      _inboundSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_inboundSpecification);
    }
    return result;
  }

}
