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
@Table(name="container_type")
public class ContainerType {
	
	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String name;
	
	private String description;
	
	@Column(name = "tare_weight")
	private float tareWeight;
	
	@Column(name = "payload_capacity")
	private float payloadCapacity;
	
	@Column(name = "cubic_capacity")
	private float cubicCapacity;
	
	@Column(name = "internal_length")
	private float internalLength;
	
	@Column(name = "internal_weight")
	private float internalWeight;
	
	@Column(name = "internal_height")
	private float internalHeight;
	
	@Column(name = "door_open_width")
	private float doorOpeningWidth;
	
	@Column(name = "door_open_height")
	private float doorOpeningHeight;
	
	@OneToMany(mappedBy = "containerType")
	private Collection<Supply> supplyList = new ArrayList<Supply>();
}
