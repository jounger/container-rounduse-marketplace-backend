package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.ContractDocument;
import com.crm.specification.ContractDocumentSpecification;

public class ContractDocumentSpecificationsBuilder extends GeneticSpecificationsBuilder<ContractDocument>{

  @Override
  public Specification<ContractDocument> build() {
    if (super.params.size() == 0) {
      return null;
    }
    ContractDocumentSpecification contractDocumentSpecification = new ContractDocumentSpecification();
    contractDocumentSpecification.setCriteria(super.params.get(0));
    Specification<ContractDocument> result = contractDocumentSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      ContractDocumentSpecification _evidenceSpecification = new ContractDocumentSpecification();
      _evidenceSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_evidenceSpecification);
    }
    return result;
  }

}
