package com.crm.models;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "shipping_line")
@PrimaryKeyJoinColumn(name = "user_id")
public class ShippingLine extends Supplier {

  @OneToMany(mappedBy = "shippingLine")
  private Collection<Supply> supplies = new ArrayList<>();
}
