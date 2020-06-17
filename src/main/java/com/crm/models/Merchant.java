package com.crm.models;

import java.util.HashSet;
import java.util.Set;

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
public class Merchant extends Supplier{

	@OneToMany(mappedBy = "merchant")
	private Set<Consignment> consignments = new HashSet<Consignment>();
	
	@OneToMany(mappedBy = "merchant")
	private Set<BiddingDocument> biddingDocuments = new HashSet<BiddingDocument>();
}
