package com.bookreview.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
public class AppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "login_id", nullable = false, length = 255, unique = true)
	private String loginId;

	@Column(name = "password", nullable = false, length = 255)
	private String password;

	@Column(name = "user_name", nullable = false, length = 100)
	private String userName;

	@Column(name = "is_admin", nullable = false)
	private Integer isAdmin;
}

