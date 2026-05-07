package com.bookreview.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bookreview.security.LoginUserPrincipal;
import com.bookreview.service.ReadingStatusService;

@Controller
public class ReadingPageController {

	private final ReadingStatusService readingStatusService;

	public ReadingPageController(ReadingStatusService readingStatusService) {
		this.readingStatusService = readingStatusService;
	}

	@GetMapping("/reading")
	public String reading(@AuthenticationPrincipal LoginUserPrincipal user, Model model) {
		model.addAttribute("books", readingStatusService.listReadingBooks(user.getUserId()));
		return "reading";
	}
}
