package com.crm.models;

import java.util.HashSet;
import java.util.Set;

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
@Table(name = "port")
public class Port {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String fullname;

	@Column(name = "name_code", unique = true)
	private String nameCode;

	private String address;
	
	@OneToMany(mappedBy = "portOfDelivery")
	private Set<Container> containers = new HashSet<Container>();

	@OneToMany(mappedBy = "portOfLoading")
	private Set<Consignment> consignments = new HashSet<Consignment>();
}
