package com.crm.models;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.crm.enums.EnumBidStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@Table(name = "bid")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, 
allowGetters = true)
public class Bid {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "bidding_document_id")
	private BiddingDocument biddingDocument;
	
	@OneToOne(mappedBy = "bid")
	private BiddingDocumentWinning biddingDocumentWinning;
	
	@ManyToOne
	@JoinColumn(name = "forwarder_id")
	private Forwarder bidder;
	
	@OneToOne
	@JoinColumn(name = "container_id")
	private Container container;

	@Column(name = "bid_price")
	private float bidPrice;
	
	@Column(name = "current_bid_price")
	private float currentBidPrice;
	
	@Column(name = "bid_date")
	private LocalDateTime bidDate;

	@Column(name = "bid_validity_period")
	private LocalDateTime bidValidityPeriod;
	
	private EnumBidStatus status;

	@Column(name = "created_at", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date createdAt;

	@Column(name = "updated_at", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	private Date updatedAt;

}
