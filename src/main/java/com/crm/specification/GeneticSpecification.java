package com.crm.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.crm.payload.request.SearchCriteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class GeneticSpecification<T> implements Specification<T> {

  private static final long serialVersionUID = 1L;

  protected SearchCriteria criteria;

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    switch (criteria.getOperation()) {
    case EQUALITY:
      return builder.equal(root.get(criteria.getKey()), criteria.getValue());
    case NEGATION:
      return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
    case GREATER_THAN:
      return builder.greaterThan(root.<String>get(criteria.getKey()), criteria.getValue().toString());
    case LESS_THAN:
      return builder.lessThan(root.<String>get(criteria.getKey()), criteria.getValue().toString());
    case LIKE:
      return builder.like(root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
    case STARTS_WITH:
      return builder.like(root.<String>get(criteria.getKey()), criteria.getValue() + "%");
    case ENDS_WITH:
      return builder.like(root.<String>get(criteria.getKey()), "%" + criteria.getValue());
    case CONTAINS:
      return builder.like(root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
    default:
      return null;
    }
  }
}
