package com.crm.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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
import javax.persistence.OneToOne;
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
@Table(name = "contract")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt", "updatedAt" }, allowGetters = true)
public class Contract {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "combined_id")
  private Combined combined;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "suppier_id")
  private Supplier sender;

  @Column(name = "price")
  private Double price;

  @Column(name = "fines_against_contract_violations")
  private Double finesAgainstContractViolations;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "discount_id")
  private Discount discount;

  @Column(name = "required")
  private Boolean required;

  @Column(name = "creation_date")
  private LocalDateTime creationDate;

  @Column(name = "payment_percentage")
  private Double paymentPercentage;

  @Column(name = "created_at", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  private Date createdAt;

  @Column(name = "updated_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  private Date updatedAt;

  @OneToMany(mappedBy = "contract")
  private Collection<Invoice> invoices = new ArrayList<>();

  @OneToMany(mappedBy = "contract")
  private Collection<ContractDocument> contractDocuments = new ArrayList<>();

  @OneToMany(mappedBy = "contract")
  private Collection<Rating> ratings = new ArrayList<>();

  @OneToMany(mappedBy = "contract")
  private Collection<ShippingInfo> shippingInfos = new ArrayList<>();
}
