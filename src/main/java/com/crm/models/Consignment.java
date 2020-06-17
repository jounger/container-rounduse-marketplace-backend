package com.crm.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
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
	
	@Column(name = "packing_time")
	private LocalDateTime packingTime;
	
	@Column(name = "booking_number")
	private String bookingNumber;
	
	@Column(name = "lay_time")
	private LocalDateTime layTime;
	
	@Column(name = "cut_of_time")
	private LocalDateTime cutOfTime;
	
	@Column(name = "payload")
	private float payload;
	
	@Column(name = "unit_of_measurment")
	private float unitOfMeasurement;
	
	private boolean fcl;
	
	@ManyToOne
	@JoinColumn(name = "address_id")
	private Address address;
	
	@ManyToOne
	@JoinColumn(name = "port_id")
	private Port port;
}
