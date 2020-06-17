package com.crm.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
@Table(name = "supplier")
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name = "user_id")
public class Supplier extends User{
	
	@Column(name = "website", length = 50)
	private String website;
	
	@Column(name = "contact_person", length = 100)
	private String contactPerson;
	
	@Column(name = "company_name", length = 100)
	private String companyName;
	
	@Column(name = "short_name", length = 10)
	private String shortName;
	
	@Column(name = "description", length = 100)
	private String description;
	
	@Column(name = "tin", length = 20)
	private String tin;
	
	@Column(name = "fax", length = 20)
	private String fax;
	
	@Column(name = "rating_value")
	private float ratingValue;
}
