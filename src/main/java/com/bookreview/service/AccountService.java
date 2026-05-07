package com.bookreview.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookreview.domain.AppUser;
import com.bookreview.repository.AppUserRepository;

@Service
public class AccountService {

	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;

	public AccountService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public void register(String loginId, String rawPassword, String userName) {
		AppUser user = new AppUser();
		user.setLoginId(loginId.trim());
		user.setPassword(passwordEncoder.encode(rawPassword));
		user.setUserName(userName.trim());
		user.setIsAdmin(0);
		appUserRepository.save(user);
	}
}

