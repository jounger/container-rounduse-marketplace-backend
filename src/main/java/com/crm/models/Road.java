package com.crm.models;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
@Table(name = "road")
public class Road {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String origin;
	
	private String destination;
	
	@Column(name = "mean_distance")
	private float meanDistance;
	
	private float duration;
	
	@Column(name = "average_velocity")
	private float averageVelocity;
	
	private float cost;
	
	@OneToMany(mappedBy = "road")
	private Collection<Quotation> quatations = new ArrayList<Quotation>();
}
