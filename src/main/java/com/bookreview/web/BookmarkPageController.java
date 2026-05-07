package com.bookreview.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bookreview.security.LoginUserPrincipal;
import com.bookreview.service.BookmarkService;

@Controller
public class BookmarkPageController {

	private final BookmarkService bookmarkService;

	public BookmarkPageController(BookmarkService bookmarkService) {
		this.bookmarkService = bookmarkService;
	}

	@GetMapping("/bookmarks")
	public String bookmarks(@AuthenticationPrincipal LoginUserPrincipal user, Model model) {
		model.addAttribute("books", bookmarkService.listBookmarkedBooks(user.getUserId()));
		return "bookmarks";
	}
}

