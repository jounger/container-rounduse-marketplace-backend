package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.ContainerType;
import com.crm.specification.ContainerTypeSpecification;

public class ContainerTypeSpecificationsBuilder extends GeneticSpecificationsBuilder<ContainerType> {

  @Override
  public Specification<ContainerType> build() {
    if (super.params.size() == 0) {
      return null;
    }
    ContainerTypeSpecification containerTypeSpecification = new ContainerTypeSpecification();
    containerTypeSpecification.setCriteria(super.params.get(0));
    Specification<ContainerType> result = containerTypeSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      ContainerTypeSpecification _containerTypeSpecification = new ContainerTypeSpecification();
      _containerTypeSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_containerTypeSpecification);
    }
    return result;
  }

}
