package com.crm.models;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@Table(name = "container")
@PrimaryKeyJoinColumn(name = "supply_id")
public class Container extends Supply{

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;
	
	@ManyToOne
	@JoinColumn(name = "forwarder_id")
	private Forwarder forwarder;
	
	@Column(name = "container_trailer")
	private String containerTrailer;

	@Column(name = "container_tractor")
	private String containerTractor;

	@Column(name = "containerNumber")
	private String containerNumber;

	@Column(name = "bl_number")
	private String blNumber;

	@Column(name = "license_plate")
	private String licensePlate;

	@Column(name = "empty_time")
	private LocalDateTime emptyTime;

	@Column(name = "pick_up_time")
	private LocalDateTime pickUpTime;

	@OneToOne
	@JoinColumn(name = "address_id")
	private Address returnStation;

	@ManyToOne
	@JoinColumn(name = "port_id")
	private Port portOfDelivery;

	@Column(name = "free_time")
	private int freeTime;

	@OneToMany(mappedBy = "container")
	private Set<Bid> bids = new HashSet<Bid>();
}
