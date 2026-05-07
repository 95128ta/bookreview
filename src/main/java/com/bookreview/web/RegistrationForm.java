package com.bookreview.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

	@NotBlank
	@Size(min = 3, max = 255)
	private String loginId;

	@NotBlank
	@Size(min = 6, max = 255)
	private String password;

	@NotBlank
	@Size(max = 100)
	private String userName;

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}

