package com.bookreview.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookreview.domain.AppUser;
import com.bookreview.repository.AppUserRepository;

@Service
public class AdminUserService {

	private final AppUserRepository appUserRepository;

	public AdminUserService(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}

	@Transactional
	public void promoteToAdmin(String loginId) {
		String trimmed = loginId == null ? "" : loginId.trim();
		if (trimmed.isEmpty()) {
			throw new IllegalArgumentException("ログイン ID を入力してください。");
		}
		AppUser user = appUserRepository.findByLoginId(trimmed)
				.orElseThrow(() -> new IllegalArgumentException("対象ユーザーが見つかりません。"));
		if (user.getIsAdmin() != null && user.getIsAdmin() != 0) {
			throw new IllegalArgumentException("既に管理者です。");
		}
		user.setIsAdmin(1);
		appUserRepository.save(user);
	}
}


