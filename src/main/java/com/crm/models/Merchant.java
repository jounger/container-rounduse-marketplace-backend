package com.crm.models;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "merchant")
@PrimaryKeyJoinColumn(name = "user_id")
public class Merchant extends Supplier{

}
