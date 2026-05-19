package com.bookreview.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookreview.security.LoginUserPrincipal;
import com.bookreview.service.AdminUserService;

@Controller
public class AdminUserController {

	private final AdminUserService adminUserService;

	public AdminUserController(AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}

	@GetMapping("/admin/users")
	public String listUsers(
			@AuthenticationPrincipal LoginUserPrincipal user,
			Model model,
			@ModelAttribute("adminMessage") String adminMessage,
			@ModelAttribute("adminError") String adminError) {
		model.addAttribute("users", adminUserService.listUsersForAdmin(user.getUserId()));
		model.addAttribute("adminMessage", adminMessage);
		model.addAttribute("adminError", adminError);
		return "admin/users";
	}

	@PostMapping("/admin/users/{userId}/promote")
	public String promoteUser(
			@PathVariable Integer userId,
			RedirectAttributes redirectAttributes) {
		try {
			adminUserService.promoteToAdminByUserId(userId);
			redirectAttributes.addFlashAttribute("adminMessage", "管理者に昇格しました。");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("adminError", ex.getMessage());
		}
		return "redirect:/admin/users";
	}

	@PostMapping("/admin/users/{userId}/demote")
	public String demoteUser(
			@PathVariable Integer userId,
			@AuthenticationPrincipal LoginUserPrincipal user,
			RedirectAttributes redirectAttributes) {
		try {
			Integer actingAdminUserId = user != null ? user.getUserId() : null;
			adminUserService.demoteFromAdminByUserId(userId, actingAdminUserId);
			redirectAttributes.addFlashAttribute("adminMessage", "一般ユーザーに降格しました。");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("adminError", ex.getMessage());
		}
		return "redirect:/admin/users";
	}
}
