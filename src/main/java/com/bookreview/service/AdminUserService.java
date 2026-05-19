package com.bookreview.service;

import java.util.Comparator;
import java.util.List;

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
		boolean canDemote = admin && !self && !(lastAdminRemains && admin);
		String demoteHint = null;
		if (admin && !canDemote) {
			if (self) {
				demoteHint = "自分自身は降格できません";
			} else if (lastAdminRemains) {
				demoteHint = "最後の管理者は降格できません";
			}
		}
		return new AdminUserRow(user, admin, self, canPromote, canDemote, demoteHint);
	}

	@Transactional
	public void promoteToAdminByUserId(Integer userId) {
		AppUser user = findUserOrThrow(userId);
		if (user.getIsAdmin() != null && user.getIsAdmin() != 0) {
			throw new IllegalArgumentException("既に管理者です。");
		}
		user.setIsAdmin(1);
		appUserRepository.save(user);
	}

	@Transactional
	public void demoteFromAdminByUserId(Integer userId, Integer actingAdminUserId) {
		AppUser user = findUserOrThrow(userId);
		if (user.getIsAdmin() == null || user.getIsAdmin() == 0) {
			throw new IllegalArgumentException("このユーザーは管理者ではありません。");
		}
		if (actingAdminUserId != null && actingAdminUserId.equals(user.getUserId())) {
			throw new IllegalArgumentException("自分自身を一般ユーザーに降格することはできません。");
		}
		if (appUserRepository.countByIsAdmin(1) <= 1) {
			throw new IllegalArgumentException("最後の管理者は降格できません。");
		}
		user.setIsAdmin(0);
		appUserRepository.save(user);
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
