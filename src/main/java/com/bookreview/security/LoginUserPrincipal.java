package com.bookreview.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bookreview.domain.AppUser;

public class LoginUserPrincipal implements UserDetails {

	private static final long serialVersionUID = 1L;

	private final Integer userId;
	private final String loginId;
	private final String passwordHash;
	private final String displayName;
	private final boolean admin;

	public LoginUserPrincipal(Integer userId, String loginId, String passwordHash, String displayName, boolean admin) {
		this.userId = userId;
		this.loginId = loginId;
		this.passwordHash = passwordHash;
		this.displayName = displayName;
		this.admin = admin;
	}

	public static LoginUserPrincipal fromEntity(AppUser user) {
		boolean isAdmin = user.getIsAdmin() != null && user.getIsAdmin() != 0;
		return new LoginUserPrincipal(
				user.getUserId(),
				user.getLoginId(),
				user.getPassword(),
				user.getUserName(),
				isAdmin);
	}

	public Integer getUserId() {
		return userId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public boolean isAdmin() {
		return admin;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (admin) {
			return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
		}
		return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}

	@Override
	public String getUsername() {
		return loginId;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}

