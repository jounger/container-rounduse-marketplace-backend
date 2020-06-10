package com.crm.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
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
@Table(name = "system_admin")
@PrimaryKeyJoinColumn(name = "user_id")
public class SystemAdmin extends User{
	
	@Column(name = "name", length = 50)
	private String name;
	
	@Column(name = "root_user")
	private Boolean rootUser;

}
