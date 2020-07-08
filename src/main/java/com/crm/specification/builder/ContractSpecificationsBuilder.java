package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.Contract;
import com.crm.specification.ContractSpecification;

public class ContractSpecificationsBuilder extends GeneticSpecificationsBuilder<Contract>{

  @Override
  public Specification<Contract> build() {
    if (super.params.size() == 0) {
      return null;
    }
    ContractSpecification contractSpecification = new ContractSpecification();
    contractSpecification.setCriteria(super.params.get(0));
    Specification<Contract> result = contractSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      ContractSpecification _contractSpecification = new ContractSpecification();
      _contractSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_contractSpecification);
    }
    return result;
  }

}
