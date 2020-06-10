package com.crm.models;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "proposal")
public class Proposal {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;
	
	@ManyToOne
	@JoinColumn(name = "consignment_id")
	private Consignment consignment;
	
	@OneToOne(mappedBy = "proposal",fetch = FetchType.EAGER)
	private Quotation quotation;
	
	private String currency;
	
	private String status;
	
	private Date createdAt;
	
	private Date updatedAt;
}
