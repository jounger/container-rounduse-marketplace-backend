package com.crm.models;

import java.util.ArrayList;
import java.util.Collection;

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
@Table(name = "driver")
@PrimaryKeyJoinColumn(name = "user_id")
public class Driver extends User{
	
	@Column(name = "fullname", length = 50)
	private String fullname;
	
	@Column(name = "driver_license", length = 50)
	private String driverLicense;
	
	@ManyToOne
	@JoinColumn(name = "forwarder_id")
	private Forwarder forwarder;
	
	@OneToOne(mappedBy = "driver")
	private Location location;
	
	@OneToMany(mappedBy = "driver")
	private Collection<Container> containers = new ArrayList<Container>();
}
