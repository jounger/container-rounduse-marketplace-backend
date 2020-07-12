package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.ContainerTractor;
import com.crm.specification.ContainerTractorSpecification;

public class ContainerTractorSpecificationsBuilder extends GeneticSpecificationsBuilder<ContainerTractor> {

  @Override
  public Specification<ContainerTractor> build() {
    if (super.params.size() == 0) {
      return null;
    }
    ContainerTractorSpecification containerTractorSpecification = new ContainerTractorSpecification();
    containerTractorSpecification.setCriteria(super.params.get(0));
    Specification<ContainerTractor> result = containerTractorSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      ContainerTractorSpecification _containerTractorSpecification = new ContainerTractorSpecification();
      _containerTractorSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_containerTractorSpecification);
    }
    return result;
  }

}
