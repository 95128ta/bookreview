package com.bookreview.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.bookreview.repository.AppUserRepository;
import com.bookreview.service.AccountService;

import jakarta.validation.Valid;

@Controller
public class RegistrationController {

	private final AppUserRepository appUserRepository;
	private final AccountService accountService;

	public RegistrationController(AppUserRepository appUserRepository, AccountService accountService) {
		this.appUserRepository = appUserRepository;
		this.accountService = accountService;
	}

	@GetMapping("/register")
	public String registerForm(Model model) {
		model.addAttribute("form", new RegistrationForm());
		return "register";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "register";
		}
		if (appUserRepository.existsByLoginId(form.getLoginId().trim())) {
			bindingResult.rejectValue("loginId", "duplicate.loginId", "このログイン ID は既に使われています。");
			return "register";
		}
		accountService.register(form.getLoginId(), form.getPassword(), form.getUserName());
		return "redirect:/login?registered";
	}
}

