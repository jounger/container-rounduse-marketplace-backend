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
@Table(name = "forwarder")
@PrimaryKeyJoinColumn(name = "user_id")
public class Forwarder extends Supplier {

  @OneToMany(mappedBy = "forwarder")
  private Collection<Driver> drivers = new ArrayList<>();

  @OneToMany(mappedBy = "bidder")
  private Collection<Bid> bids = new ArrayList<>();

  @OneToMany(mappedBy = "forwarder")
  private Collection<Inbound> inbounds = new ArrayList<>();

  @OneToMany(mappedBy = "forwarder")
  private Collection<Vehicle> vehicles = new ArrayList<>();
}
