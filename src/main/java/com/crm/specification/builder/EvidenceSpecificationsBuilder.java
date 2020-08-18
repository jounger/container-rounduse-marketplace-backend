package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.ContractDocument;
import com.crm.specification.EvidenceSpecification;

public class EvidenceSpecificationsBuilder extends GeneticSpecificationsBuilder<ContractDocument>{

  @Override
  public Specification<ContractDocument> build() {
    if (super.params.size() == 0) {
      return null;
    }
    EvidenceSpecification evidenceSpecification = new EvidenceSpecification();
    evidenceSpecification.setCriteria(super.params.get(0));
    Specification<ContractDocument> result = evidenceSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      EvidenceSpecification _evidenceSpecification = new EvidenceSpecification();
      _evidenceSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_evidenceSpecification);
    }
    return result;
  }

}
