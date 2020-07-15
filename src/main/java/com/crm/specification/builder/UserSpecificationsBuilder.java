package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.User;
import com.crm.specification.UserSpecification;

public class UserSpecificationsBuilder extends GeneticSpecificationsBuilder<User> {

  @Override
  public Specification<User> build() {
    if (super.params.size() == 0) {
      return null;
    }
    UserSpecification userSpecification = new UserSpecification();
    userSpecification.setCriteria(super.params.get(0));
    Specification<User> result = userSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      UserSpecification _userSpecification = new UserSpecification();
      _userSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_userSpecification);
    }
    return result;
  }

}
