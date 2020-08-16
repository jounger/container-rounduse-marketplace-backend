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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
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
@Table(name = "payment")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt" }, allowGetters = true)
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;
  
  @ManyToOne
  @JoinColumn(name = "recipient_id")
  private Supplier recipient;
  
  @ManyToOne
  @JoinColumn(name = "sender_id")
  private Supplier sender;

  @ManyToOne
  @JoinColumn(name = "contract_id")
  private Contract contract;

  @Column(name = "detail")
  @Lob
  @Size(min = 2)
  private String detail;

  @Column(name = "amount")
  private Double amount;

  @Column(name = "is_paid")
  private Boolean isPaid;

  // EnumPaymentType
  @Column(name = "type")
  @Size(min = 2, max = 10)
  private String type;

  @Column(name = "payment_date")
  private LocalDateTime paymentDate;

  @Column(name = "created_at", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  private Date createdAt;
}
