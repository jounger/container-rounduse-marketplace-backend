package com.crm.models;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "merchant")
@PrimaryKeyJoinColumn(name = "user_id")
public class Merchant extends Supplier {

  @OneToMany(mappedBy = "merchant")
  private Collection<Outbound> outbounds = new ArrayList<>();

  @OneToMany(mappedBy = "offeree")
  private Collection<BiddingDocument> biddingDocuments = new ArrayList<>();
}
