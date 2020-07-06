package com.crm.models;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Size;

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
@Table(name = "container_semi_trailer")
@PrimaryKeyJoinColumn(name = "vehicle_id")
public class ContainerSemiTrailer extends Vehicle {

  // EnumTrailerType
  @Column(name = "type")
  @Size(min = 2, max = 3)
  private String type;

  // EnumUnit
  @Column(name = "unit_of_measurement")
  private String unitOfMeasurement;

  @OneToMany(mappedBy = "tractor")
  private Collection<Container> containers = new ArrayList<>();

}
