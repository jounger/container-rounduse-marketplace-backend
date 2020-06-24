package com.crm.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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

  @OneToMany(mappedBy = "forwarder", fetch = FetchType.LAZY)
  private Set<Driver> drivers = new HashSet<>();

  @OneToMany(mappedBy = "bidder", fetch = FetchType.LAZY)
  private Set<Bid> bids = new HashSet<Bid>();

  @OneToMany(mappedBy = "forwarder")
  private Set<Inbound> inbounds = new HashSet<>();
}
