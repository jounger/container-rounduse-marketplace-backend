package com.crm.models;

import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	
	private String containerTrailer;
	
	private String containerTractor;
	
	private String containerNumber;
	
	private String blNumber;
	
	private String licensePlate;
	
	private Date emptyTime;
	
	@OneToOne
	@JoinColumn(name = "address_id")
	private Address returnStation;
	
	@ManyToOne
	@JoinColumn(name = "port_id")
	private Port port;
	
	private int feeDET;
	
	@OneToOne(mappedBy = "container")
	private ProposalDetail proposalDetail;
}
