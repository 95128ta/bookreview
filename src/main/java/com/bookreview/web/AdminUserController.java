package com.bookreview.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookreview.security.LoginUserPrincipal;
import com.bookreview.service.AdminUserService;

@Controller
public class AdminUserController {

	private final AdminUserService adminUserService;

	public AdminUserController(AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}

	@PostMapping("/admin/users/promote")
	public String promoteUser(
			@RequestParam("loginId") String loginId,
			RedirectAttributes redirectAttributes) {
		try {
			adminUserService.promoteToAdmin(loginId);
			redirectAttributes.addFlashAttribute("adminMessage", "管理者に昇格しました。");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("adminError", ex.getMessage());
		}
		return "redirect:/admin/books";
	}

	@PostMapping("/admin/users/demote")
	public String demoteUser(
			@RequestParam("loginId") String loginId,
			@AuthenticationPrincipal LoginUserPrincipal user,
			RedirectAttributes redirectAttributes) {
		try {
			Integer actingAdminUserId = user != null ? user.getUserId() : null;
			adminUserService.demoteFromAdmin(loginId, actingAdminUserId);
			redirectAttributes.addFlashAttribute("adminMessage", "一般ユーザーに降格しました。");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("adminError", ex.getMessage());
		}
		return "redirect:/admin/books";
	}
}


