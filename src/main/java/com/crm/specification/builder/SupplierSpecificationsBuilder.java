package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.Supplier;
import com.crm.specification.SupplierSpecification;

public class SupplierSpecificationsBuilder extends GeneticSpecificationsBuilder<Supplier>{

  @Override
  public Specification<Supplier> build() {
    if (super.params.size() == 0) {
      return null;
    }
    SupplierSpecification supplierSpecification = new SupplierSpecification();
    supplierSpecification.setCriteria(super.params.get(0));
    Specification<Supplier> result = supplierSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      SupplierSpecification _supplierSpecification = new SupplierSpecification();
      _supplierSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_supplierSpecification);
    }
    return result;
  }
}
