package com.bookreview.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookreview.security.LoginUserPrincipal;
import com.bookreview.service.ReviewService;

@Controller
public class MyReviewsController {

	private final ReviewService reviewService;

	public MyReviewsController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@GetMapping("/my-reviews")
	public String myReviews(
			@AuthenticationPrincipal LoginUserPrincipal user,
			Model model,
			@ModelAttribute("myReviewMessage") String myReviewMessage,
			@ModelAttribute("myReviewError") String myReviewError) {
		model.addAttribute("myReviews", reviewService.listMyReviews(user.getUserId()));
		model.addAttribute("myReviewMessage", myReviewMessage);
		model.addAttribute("myReviewError", myReviewError);
		return "my-reviews";
	}

	@PostMapping("/my-reviews/{reviewId}/update")
	public String updateMyReview(
			@PathVariable Integer reviewId,
			@RequestParam("rating") double rating,
			@RequestParam(value = "comment", required = false) String comment,
			@RequestParam(value = "spoiler", defaultValue = "false") boolean spoiler,
			@AuthenticationPrincipal LoginUserPrincipal user,
			RedirectAttributes redirectAttributes) {
		try {
			reviewService.updateMyReview(reviewId, user.getUserId(), rating, comment, spoiler);
			redirectAttributes.addFlashAttribute("myReviewMessage", "レビューを更新しました。");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("myReviewError", ex.getMessage());
		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("myReviewError", "レビューを更新できませんでした。");
		}
		return "redirect:/my-reviews";
	}

	@PostMapping("/my-reviews/{reviewId}/delete")
	public String deleteMyReview(
			@PathVariable Integer reviewId,
			@AuthenticationPrincipal LoginUserPrincipal user,
			RedirectAttributes redirectAttributes) {
		try {
			reviewService.deleteMyReview(reviewId, user.getUserId());
			redirectAttributes.addFlashAttribute("myReviewMessage", "レビューを削除しました。");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("myReviewError", ex.getMessage());
		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("myReviewError", "レビューを削除できませんでした。");
		}
		return "redirect:/my-reviews";
	}
}
