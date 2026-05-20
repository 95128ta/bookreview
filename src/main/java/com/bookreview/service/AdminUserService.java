package com.bookreview.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookreview.domain.AppUser;
import com.bookreview.repository.AppUserRepository;

@Service
public class AdminUserService {

	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;

	public AdminUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public List<AdminUserRow> listUsersForAdmin(Integer currentAdminUserId) {
		long adminCount = appUserRepository.countByIsAdmin(1);
		boolean lastAdminRemains = adminCount <= 1;
		return appUserRepository.findAll().stream()
				.sorted(Comparator
						.comparing((AppUser u) -> u.getIsAdmin() != null && u.getIsAdmin() != 0).reversed()
						.thenComparing(AppUser::getLoginId, String.CASE_INSENSITIVE_ORDER))
				.map(user -> toRow(user, currentAdminUserId, lastAdminRemains))
				.toList();
	}

	private static AdminUserRow toRow(AppUser user, Integer currentAdminUserId, boolean lastAdminRemains) {
		boolean admin = user.getIsAdmin() != null && user.getIsAdmin() != 0;
		boolean self = currentAdminUserId != null && currentAdminUserId.equals(user.getUserId());
		boolean canPromote = !admin;
		boolean canDemote = admin && !(lastAdminRemains && admin);
		String demoteHint = null;
		if (admin && !canDemote && lastAdminRemains) {
			demoteHint = "最後の管理者は降格できません";
		} else if (admin && canDemote && self) {
			demoteHint = "実行後はログアウトされます";
		}
		return new AdminUserRow(user, admin, self, canPromote, canDemote, demoteHint);
	}

	public void verifyTargetUserPassword(Integer targetUserId, String rawPassword) {
		if (rawPassword == null || rawPassword.isBlank()) {
			throw new IllegalArgumentException("対象ユーザーのパスワードを入力してください。");
		}
		AppUser target = findUserOrThrow(targetUserId);
		if (!passwordEncoder.matches(rawPassword, target.getPassword())) {
			throw new IllegalArgumentException("対象ユーザーのパスワードが一致しません。");
		}
	}

	@Transactional
	public void promoteToAdminByUserId(Integer userId, String targetUserPassword) {
		verifyTargetUserPassword(userId, targetUserPassword);
		AppUser user = findUserOrThrow(userId);
		if (user.getIsAdmin() != null && user.getIsAdmin() != 0) {
			throw new IllegalArgumentException("既に管理者です。");
		}
		user.setIsAdmin(1);
		appUserRepository.save(user);
	}

	/**
	 * @return 操作対象が自分自身だった場合 true（呼び出し側でログアウト処理）
	 */
	@Transactional
	public boolean demoteFromAdminByUserId(Integer userId, Integer actingAdminUserId, String targetUserPassword) {
		verifyTargetUserPassword(userId, targetUserPassword);
		AppUser user = findUserOrThrow(userId);
		if (user.getIsAdmin() == null || user.getIsAdmin() == 0) {
			throw new IllegalArgumentException("このユーザーは管理者ではありません。");
		}
		if (appUserRepository.countByIsAdmin(1) <= 1) {
			throw new IllegalArgumentException("最後の管理者は降格できません。");
		}
		user.setIsAdmin(0);
		appUserRepository.save(user);
		return actingAdminUserId != null && actingAdminUserId.equals(user.getUserId());
	}

	private AppUser findUserOrThrow(Integer userId) {
		if (userId == null) {
			throw new IllegalArgumentException("ユーザー ID が不正です。");
		}
		return appUserRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("対象ユーザーが見つかりません。"));
	}

	public record AdminUserRow(
			AppUser user,
			boolean admin,
			boolean self,
			boolean canPromote,
			boolean canDemote,
			String demoteHint) {
	}
}
