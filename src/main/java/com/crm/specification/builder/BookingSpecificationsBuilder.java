package com.crm.specification.builder;

import org.springframework.data.jpa.domain.Specification;

import com.crm.models.Booking;
import com.crm.specification.BookingSpecification;

public class BookingSpecificationsBuilder extends GeneticSpecificationsBuilder<Booking> {

  @Override
  public Specification<Booking> build() {
    if (super.params.size() == 0) {
      return null;
    }
    BookingSpecification bookingSpecification = new BookingSpecification();
    bookingSpecification.setCriteria(super.params.get(0));
    Specification<Booking> result = bookingSpecification;
    for (int i = 1; i < super.params.size(); i++) {
      BookingSpecification _bookingSpecification = new BookingSpecification();
      _bookingSpecification.setCriteria(super.params.get(i));
      result = Specification.where(result).and(_bookingSpecification);
    }
    return result;
  }

}
