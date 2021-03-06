package com.crm.models;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
@Table(name = "rating")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt" }, allowGetters = true)
public class Rating {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_supplier_id")
  private Supplier sender;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "receiver_supplier_id")
  private Supplier receiver;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "contract_id")
  private Contract contract;

  @Column(name = "rating_value")
  private Integer ratingValue;

  @Column(name = "comment")
  private String comment;

  @Column(name = "created_at", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  private Date createdAt;
}
