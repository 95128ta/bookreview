package com.bookreview.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
}


