package com.bookreview.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookreview.domain.AppUser;
import com.bookreview.repository.AppUserRepository;
import com.bookreview.repository.BookmarkRepository;
import com.bookreview.repository.ReadingStatusRepository;
import com.bookreview.repository.ReviewRepository;
import com.bookreview.security.LoginUserPrincipal;

@Service
public class ProfileService {

	private final AppUserRepository appUserRepository;
	private final ReviewRepository reviewRepository;
	private final BookmarkRepository bookmarkRepository;
	private final ReadingStatusRepository readingStatusRepository;
	private final PasswordEncoder passwordEncoder;

	public ProfileService(
			AppUserRepository appUserRepository,
			ReviewRepository reviewRepository,
			BookmarkRepository bookmarkRepository,
			ReadingStatusRepository readingStatusRepository,
			PasswordEncoder passwordEncoder) {
		this.appUserRepository = appUserRepository;
		this.reviewRepository = reviewRepository;
		this.bookmarkRepository = bookmarkRepository;
		this.readingStatusRepository = readingStatusRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public ProfileStats statsFor(Integer userId) {
		return new ProfileStats(
				reviewRepository.countByUserId(userId),
				bookmarkRepository.countByUserId(userId),
				readingStatusRepository.countByUserId(userId));
	}

	@Transactional
	public void updateDisplayName(Integer userId, String newDisplayName) {
		String trimmed = newDisplayName == null ? "" : newDisplayName.trim();
		if (trimmed.isEmpty() || trimmed.length() > 100) {
			throw new IllegalArgumentException("表示名は1～100文字で入力してください。");
		}
		AppUser user = appUserRepository.findById(userId).orElseThrow();
		user.setUserName(trimmed);
		appUserRepository.save(user);
		refreshAuthentication(userId);
	}

	@Transactional
	public void changePassword(Integer userId, String currentPassword, String newPassword) {
		if (newPassword == null || newPassword.length() < 6) {
			throw new IllegalArgumentException("新しいパスワードは6文字以上にしてください。");
		}
		AppUser user = appUserRepository.findById(userId).orElseThrow();
		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new IllegalArgumentException("現在のパスワードが一致しません。");
		}
		user.setPassword(passwordEncoder.encode(newPassword));
		appUserRepository.save(user);
		refreshAuthentication(userId);
	}

	@Transactional
	public void withdraw(Integer userId, String password) {
		AppUser user = appUserRepository.findById(userId).orElseThrow();
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new IllegalArgumentException("パスワードが一致しません。");
		}
		appUserRepository.deleteById(userId);
	}

	private void refreshAuthentication(Integer userId) {
		AppUser user = appUserRepository.findById(userId).orElseThrow();
		LoginUserPrincipal principal = LoginUserPrincipal.fromEntity(user);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
				principal, null, principal.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	public record ProfileStats(long reviewCount, long bookmarkCount, long readingStatusCount) {
	}
}

