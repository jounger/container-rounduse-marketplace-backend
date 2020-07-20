package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.ContainerSemiTrailer;
import com.crm.specification.ContainerSemiTrailerSpecification;

public class ContainerSemiTrailerSpecificationsBuilder extends GeneticSpecificationsBuilder<ContainerSemiTrailer> {

  @Override
  public Specification<ContainerSemiTrailer> build() {
    if (super.params.size() == 0) {
      return null;
    }
    ContainerSemiTrailerSpecification containerSemiTrailerSpecification = new ContainerSemiTrailerSpecification();
    containerSemiTrailerSpecification.setCriteria(super.params.get(0));
    Specification<ContainerSemiTrailer> result = containerSemiTrailerSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      ContainerSemiTrailerSpecification _containerSemiTrailerSpecification = new ContainerSemiTrailerSpecification();
      _containerSemiTrailerSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_containerSemiTrailerSpecification);
    }
    return result;
  }

}
