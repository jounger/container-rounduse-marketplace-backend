package com.crm.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
public class ShippingLine extends User{
	
	private String website;
	
	@Column(name = "company_name", length = 50)
	private String companyName;
	
	@Column(name = "short_name", length = 10)
	private String shortName;
	
	@ManyToMany
	@JoinTable(name = "shipping_line_icd", 
				joinColumns = @JoinColumn(name = "shipping_line_id"),
				inverseJoinColumns = @JoinColumn(name = "icd_id"))
	private Collection<Icd> icdList = new ArrayList<Icd>();
	
	@OneToMany(mappedBy = "shippingLine")
	private Set<Consignment> consignmentList = new HashSet<Consignment>();
}
