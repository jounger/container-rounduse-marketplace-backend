package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.BillOfLading;
import com.crm.specification.BillOfLadingSpecification;

public class BillOfLadingSpecificationsBuilder extends GeneticSpecificationsBuilder<BillOfLading> {

  @Override
  public Specification<BillOfLading> build() {
    if (super.params.size() == 0) {
      return null;
    }
    BillOfLadingSpecification billOfLadingSpecification = new BillOfLadingSpecification();
    billOfLadingSpecification.setCriteria(super.params.get(0));
    Specification<BillOfLading> result = billOfLadingSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      BillOfLadingSpecification _billOfLadingSpecification = new BillOfLadingSpecification();
      _billOfLadingSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_billOfLadingSpecification);
    }
    return result;
  }

}
