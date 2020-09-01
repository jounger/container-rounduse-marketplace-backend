package com.crm.models;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
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
@Table(name = "feedback")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt" }, allowGetters = true)
public class Feedback {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id")
  private User sender;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipient_id")
  private User recipient;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "report_id")
  private Report report;

  @Column(name = "message")
  @NotBlank
  @Lob
  @Size(min = 2)
  private String message;

  @Column(name = "satisfaction_points")
  private Integer satisfactionPoints;

  @Column(name = "send_date")
  private LocalDateTime sendDate;

  @Column(name = "created_at", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  private Date createdAt;

}
