package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.Evidence;
import com.crm.specification.EvidenceSpecification;

public class EvidenceSpecificationsBuilder extends GeneticSpecificationsBuilder<Evidence>{

  @Override
  public Specification<Evidence> build() {
    if (super.params.size() == 0) {
      return null;
    }
    EvidenceSpecification evidenceSpecification = new EvidenceSpecification();
    evidenceSpecification.setCriteria(super.params.get(0));
    Specification<Evidence> result = evidenceSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      EvidenceSpecification _evidenceSpecification = new EvidenceSpecification();
      _evidenceSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_evidenceSpecification);
    }
    return result;
  }

}
