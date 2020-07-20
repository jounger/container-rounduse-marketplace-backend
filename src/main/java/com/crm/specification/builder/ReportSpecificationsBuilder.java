package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.Report;
import com.crm.specification.ReportSpecification;

public class ReportSpecificationsBuilder extends GeneticSpecificationsBuilder<Report>{

  @Override
  public Specification<Report> build() {
    if (super.params.size() == 0) {
      return null;
    }
    ReportSpecification reportSpecification = new ReportSpecification();
    reportSpecification.setCriteria(super.params.get(0));
    Specification<Report> result = reportSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      ReportSpecification _reportSpecification = new ReportSpecification();
      _reportSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_reportSpecification);
    }
    return result;
  }

}
