package com.bookreview.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.bookreview.security.LoginUserPrincipal;
import com.bookreview.service.BookService;
import com.bookreview.service.ReadingStatusService;

@Controller
public class HomeController {

	private final BookService bookService;
	private final ReadingStatusService readingStatusService;

	public HomeController(BookService bookService, ReadingStatusService readingStatusService) {
		this.bookService = bookService;
		this.readingStatusService = readingStatusService;
	}

	@GetMapping({"/", "/menu"})
	public String index(
			Model model,
			@ModelAttribute("withdrawnMessage") String withdrawnMessage,
			@AuthenticationPrincipal LoginUserPrincipal user) {
		model.addAttribute("recentBooks", bookService.getRecentBooks());
		model.addAttribute("recommendedBooks", bookService.getRecommendedBooks());
		model.addAttribute("withdrawnMessage", withdrawnMessage);
		if (user != null) {
			model.addAttribute("readingCount", readingStatusService.countReadingBooks(user.getUserId()));
		}
		return "index";
	}
}

