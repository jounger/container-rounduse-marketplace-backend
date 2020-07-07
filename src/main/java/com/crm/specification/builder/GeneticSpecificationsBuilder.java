package com.crm.specification.builder;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import com.crm.enums.EnumSearchOperation;
import com.crm.payload.request.SearchCriteria;

public abstract class GeneticSpecificationsBuilder<T> {
  
  private static final Logger logger = LoggerFactory.getLogger(GeneticSpecificationsBuilder.class);

  protected List<SearchCriteria> params = new ArrayList<>();

  public GeneticSpecificationsBuilder<T> with(String key, String operation, Object value) {
    EnumSearchOperation op = EnumSearchOperation.getSimpleOperation(operation.charAt(0));
    if (op != null) {
      this.params.add(new SearchCriteria(key, op, value));
      logger.info("operation: {}", op.name());
      logger.info("params: {}", params);
    }
    return this;
  }

  public abstract Specification<T> build();
}
