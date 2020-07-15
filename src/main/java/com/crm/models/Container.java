package com.crm.models;

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
@Table(name = "container")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt", "updatedAt" }, allowGetters = true)
public class Container {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "driver_id")
  private Driver driver;

  @ManyToOne
  @JoinColumn(name = "bill_of_lading_id")
  private BillOfLading billOfLading;

  @Column(name = "container_number")
  private String containerNumber;

  @ManyToOne
  @JoinColumn(name = "container_semi_trailer_id")
  private ContainerSemiTrailer trailer;

  @ManyToOne
  @JoinColumn(name = "container_tractor_id")
  private ContainerTractor tractor;

  // EnumSupplyStatus
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

  @ManyToMany(mappedBy = "containers")
  private Collection<Bid> bids = new ArrayList<>();

  // DO NOT DELETE CODE BELLOW

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((containerNumber == null) ? 0 : containerNumber.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Container other = (Container) obj;
    if (containerNumber == null) {
      if (other.containerNumber != null)
        return false;
    } else if (!containerNumber.equals(other.containerNumber))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

}
