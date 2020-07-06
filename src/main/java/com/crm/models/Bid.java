package com.crm.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "bid")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt", "updatedAt" }, allowGetters = true)
public class Bid {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "bidding_document_id")
  private BiddingDocument biddingDocument;

  @ManyToOne
  @JoinColumn(name = "forwarder_id")
  private Forwarder bidder;

  @ManyToMany
  @JoinTable(name = "bid_container", joinColumns = @JoinColumn(name = "bid_id"), inverseJoinColumns = @JoinColumn(name = "container_id"))
  private Collection<Container> containers = new ArrayList<>();

  @Column(name = "bid_price")
  private Double bidPrice;

  @Column(name = "bid_date")
  private LocalDateTime bidDate;

  @Column(name = "bid_validity_period")
  private LocalDateTime bidValidityPeriod;

  @Column(name = "date_of_decision")
  private LocalDateTime dateOfDecision;

  // EnumBidStatus
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
}
