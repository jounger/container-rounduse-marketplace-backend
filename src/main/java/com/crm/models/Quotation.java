package com.crm.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
@Table(name = "quotation")
public class Quotation {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@OneToOne
	@JoinColumn(name = "quotation")
	private Proposal proposal;
	
	@ManyToOne
	@JoinColumn(name = "quotations")
	private Road road;
	
	@Column(name = "container_type")
	private String containerType;
	
	private boolean flc;
	
	private int vat;
	
	@Column(name = "total_cost")
	private float totalCost;
}
