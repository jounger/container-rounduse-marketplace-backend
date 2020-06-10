package com.crm.models;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "consignment")
@PrimaryKeyJoinColumn(name = "supply_id")
public class Consignment extends Supply{
	
	@ManyToOne
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;
	
	@ManyToMany
	@JoinTable(name = "consignment_category",
				joinColumns = @JoinColumn(name = "consignment_id"),
				inverseJoinColumns = @JoinColumn(name = "category_id"))
	private Set<Category> categoryList = new HashSet<Category>();
	
	private Date packingTime;
	
	private String bookingNumber;
	
	private Date layTime;
	
	private Date cutOfTime;
	
	private float payload;
	
	private float unitOfMeasurement;
	
	private boolean flc;
	
	@ManyToOne
	@JoinColumn(name = "address_id")
	private Address address;
	
	@ManyToOne
	@JoinColumn(name = "port_id")
	private Port port;
}
