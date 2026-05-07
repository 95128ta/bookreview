package com.bookreview.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookreview.service.ReviewService;

@Controller
public class AdminReviewController {

	private final ReviewService reviewService;

	public AdminReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@PostMapping("/admin/books/{bookId}/reviews/{reviewId}/delete")
	public String deleteReview(
			@PathVariable Integer bookId,
			@PathVariable Integer reviewId,
			RedirectAttributes redirectAttributes) {
		try {
			reviewService.deleteReviewForAdmin(bookId, reviewId);
			redirectAttributes.addFlashAttribute("adminMessage", "レビューを削除しました。");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("adminError", ex.getMessage());
		}
		return "redirect:/books/" + bookId;
	}
}

