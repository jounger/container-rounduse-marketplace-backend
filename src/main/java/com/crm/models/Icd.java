package com.crm.models;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
		@UniqueConstraint(columnNames = "name")
})
public class Icd {
	
	private String fullname;
	
	@Column(name = "name_code", unique = true)
	private String nameCode;
	
	private String address;
	
	@ManyToMany(mappedBy = "icds")
	private Collection<ShippingLine> shippingLines = new ArrayList<ShippingLine>();
}
