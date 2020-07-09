package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.Outbound;
import com.crm.specification.OutboundSpecification;

public class OutboundSpecificationsBuilder extends GeneticSpecificationsBuilder<Outbound> {

  @Override
  public Specification<Outbound> build() {
    if (super.params.size() == 0) {
      return null;
    }
    OutboundSpecification outboundSpecification = new OutboundSpecification();
    outboundSpecification.setCriteria(super.params.get(0));
    Specification<Outbound> result = outboundSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      OutboundSpecification _outboundSpecification = new OutboundSpecification();
      _outboundSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_outboundSpecification);
    }
    return result;
  }

}
