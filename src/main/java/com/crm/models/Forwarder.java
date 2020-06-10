package com.crm.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "forwarder")
public class Forwarder extends Supplier{
	
	private String contact;
	
	private String bankAccount;
	
	@OneToMany(mappedBy = "forwarder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Driver> drivers = new HashSet<Driver>();
}
