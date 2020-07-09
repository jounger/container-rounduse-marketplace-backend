package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.Feedback;
import com.crm.specification.FeedbackSpecification;

public class FeedbackSpecificationsBuilder extends GeneticSpecificationsBuilder<Feedback>{

  @Override
  public Specification<Feedback> build() {
    if (super.params.size() == 0) {
      return null;
    }
    FeedbackSpecification feedbackSpecification = new FeedbackSpecification();
    feedbackSpecification.setCriteria(super.params.get(0));
    Specification<Feedback> result = feedbackSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      FeedbackSpecification _feedbackSpecification = new FeedbackSpecification();
      _feedbackSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_feedbackSpecification);
    }
    return result;
  }

}
