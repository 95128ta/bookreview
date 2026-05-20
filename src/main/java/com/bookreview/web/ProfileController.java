package com.bookreview.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookreview.domain.AppUser;
import com.bookreview.repository.AppUserRepository;
import com.bookreview.security.LoginUserPrincipal;
import com.bookreview.service.ProfileService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProfileController {

	private final AppUserRepository appUserRepository;
	private final ProfileService profileService;

	public ProfileController(AppUserRepository appUserRepository, ProfileService profileService) {
		this.appUserRepository = appUserRepository;
		this.profileService = profileService;
	}

	@GetMapping("/profile")
	public String profile(
			@AuthenticationPrincipal LoginUserPrincipal user,
			Model model,
			@ModelAttribute("profileMessage") String profileMessage,
			@ModelAttribute("profileError") String profileError) {
		AppUser entity = appUserRepository.findById(user.getUserId()).orElseThrow();
		model.addAttribute("loginId", entity.getLoginId());
		model.addAttribute("displayName", entity.getUserName());
		model.addAttribute("admin", entity.getIsAdmin() != null && entity.getIsAdmin() != 0);
		model.addAttribute("stats", profileService.statsFor(user.getUserId()));
		model.addAttribute("profileMessage", profileMessage);
		model.addAttribute("profileError", profileError);
		return "profile";
	}

	@PostMapping("/profile/name")
	public String updateName(
			@AuthenticationPrincipal LoginUserPrincipal user,
			@RequestParam("userName") String userName,
			RedirectAttributes redirectAttributes) {
		try {
			profileService.updateDisplayName(user.getUserId(), userName);
			redirectAttributes.addFlashAttribute("profileMessage", "表示名を更新しました。");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("profileError", ex.getMessage());
		}
		return "redirect:/profile";
	}

	@PostMapping("/profile/password")
	public String updatePassword(
			@AuthenticationPrincipal LoginUserPrincipal user,
			@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword,
			RedirectAttributes redirectAttributes) {
		try {
			profileService.changePassword(user.getUserId(), currentPassword, newPassword);
			redirectAttributes.addFlashAttribute("profileMessage", "パスワードを変更しました。");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("profileError", ex.getMessage());
		}
		return "redirect:/profile";
	}

	@PostMapping("/profile/withdraw")
	public String withdraw(
			@AuthenticationPrincipal LoginUserPrincipal user,
			@RequestParam("password") String password,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		try {
			profileService.withdraw(user.getUserId(), password);
			session.invalidate();
			SecurityContextHolder.clearContext();
			redirectAttributes.addFlashAttribute("withdrawnMessage", "退会処理が完了しました。");
			return "redirect:/";
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("profileError", ex.getMessage());
			return "redirect:/profile";
		}
	}
}
