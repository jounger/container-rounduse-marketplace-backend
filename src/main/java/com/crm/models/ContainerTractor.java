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
@Table(name = "container_tractor")
@PrimaryKeyJoinColumn(name = "vehicle_id")
public class ContainerTractor extends Vehicle {

  @OneToMany(mappedBy = "tractor")
  private Collection<Container> containers = new ArrayList<>();

}
