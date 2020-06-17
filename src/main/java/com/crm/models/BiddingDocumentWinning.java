package com.crm.models;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@Table(name = "bidding_document_winning")
public class BiddingDocumentWinning {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bidding_document_id")
	private BiddingDocument biddingDocument;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bid_id")
	private Bid bid;
	
	private LocalDateTime dateOfDecision;
	
}