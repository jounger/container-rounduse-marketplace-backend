package com.crm.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@Table(name = "bidding_document")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt", "updatedAt" }, allowGetters = true)
public class BiddingDocument {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "merchant_id")
  private Merchant offeree;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "outbound_id")
  private Outbound outbound;

  @Column(name = "is_multiple_award")
  private Boolean isMultipleAward;

  @Column(name = "bid_opening")
  private LocalDateTime bidOpening;

  @Column(name = "bid_closing")
  private LocalDateTime bidClosing;

  // EnumCurrency
  @Column(name = "currency_of_payment")
  private String currencyOfPayment;

  @Column(name = "bid_package_price")
  private Double bidPackagePrice;

  @Column(name = "bid_floor_price")
  private Double bidFloorPrice;

  @Column(name = "price_leadership")
  private Double priceLeadership;

  @Column(name = "status")
  private String status;

  @Column(name = "created_at", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  private Date createdAt;

  @Column(name = "updated_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  private Date updatedAt;

  @OneToMany(mappedBy = "biddingDocument")
  private Collection<Bid> bids = new ArrayList<>();

  @OneToMany(mappedBy = "report")
  private Collection<Report> reports = new ArrayList<>();

  @OneToMany(mappedBy = "relatedResource", cascade = CascadeType.REMOVE)
  private Collection<BiddingNotification> biddingNotifications = new ArrayList<>();
}
