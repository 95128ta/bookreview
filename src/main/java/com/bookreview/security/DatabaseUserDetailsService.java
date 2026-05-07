package com.bookreview.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bookreview.repository.AppUserRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

	private final AppUserRepository appUserRepository;

	public DatabaseUserDetailsService(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
		return appUserRepository.findByLoginId(loginId.trim())
				.map(LoginUserPrincipal::fromEntity)
				.orElseThrow(() -> new UsernameNotFoundException("user not found: " + loginId));
	}
}

