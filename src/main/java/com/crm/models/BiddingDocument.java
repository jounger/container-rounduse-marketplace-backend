package com.crm.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.crm.enums.EnumCurrency;

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
@Table(name = "bidding_document")
public class BiddingDocument {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;
	
	@OneToOne(mappedBy = "biddingDocument")
	private NotificationOfAward notificationOfAward;
	
	@ManyToOne
	@JoinColumn(name = "consignment_id")
	private Consignment consignment;
	
	@OneToMany(fetch = FetchType.LAZY ,mappedBy = "biddingDocument")
	private List<Bid> bids = new ArrayList<Bid>();
	
	@OneToMany(mappedBy = "report")
	private Collection<Report> reports = new ArrayList<Report>();
	
	@Column(name = "bid_opening")
	private LocalDateTime bidOpening;
	
	@Column(name = "bid_closing")
	private LocalDateTime bidClosing;
	
	@Column(name = "currency_of_payment")
	private EnumCurrency currencyOfPayment;
	
	@Column(name = "bid_package_price")
	private float bidPackagePrice;
	
	@Column(name = "bit_floor_price")
	private float bitFloorPrice;
	
	@Column(name = "bit_step")
	private float bitStep;
	
	@ManyToOne
	@JoinColumn(name = "discount_id")
	private Discount bidDiscountCode;
	
	@Column(name = "price_leadership")
	private float priceLeadership;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date createdAt;
	
	@Column(name = "updated_at", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	private Date updatedAt;
}