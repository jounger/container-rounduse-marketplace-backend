package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.Notification;
import com.crm.specification.NotificationSpecification;

public class NotificationSpecificationsBuilder extends GeneticSpecificationsBuilder<Notification> {

  @Override
  public Specification<Notification> build() {
    if (super.params.size() == 0) {
      return null;
    }
    NotificationSpecification notificationSpecification = new NotificationSpecification();
    notificationSpecification.setCriteria(super.params.get(0));
    Specification<Notification> result = notificationSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      NotificationSpecification _notificationSpecification = new NotificationSpecification();
      _notificationSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_notificationSpecification);
    }
    return result;
  }

}
