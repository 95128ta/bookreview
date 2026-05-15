package com.bookreview.web;

import java.nio.charset.StandardCharsets;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.bookreview.security.LoginUserPrincipal;
import com.bookreview.service.BookService;
import com.bookreview.service.BookmarkService;
import com.bookreview.service.ReadingStatusService;
import com.bookreview.service.ReviewService;

@Controller
public class BookController {

	private final BookService bookService;
	private final ReviewService reviewService;
	private final BookmarkService bookmarkService;
	private final ReadingStatusService readingStatusService;

	public BookController(
			BookService bookService,
			ReviewService reviewService,
			BookmarkService bookmarkService,
			ReadingStatusService readingStatusService) {
		this.bookService = bookService;
		this.reviewService = reviewService;
		this.bookmarkService = bookmarkService;
		this.readingStatusService = readingStatusService;
	}

	@GetMapping("/books")
	public String books(@RequestParam(value = "q", required = false) String q, Model model) {
		String query = q == null ? "" : q.trim();
		model.addAttribute("books", bookService.searchBooks(query));
		model.addAttribute("query", query);
		model.addAttribute("searchActive", !query.isEmpty());
		return "books";
	}

	@GetMapping("/search")
	public String searchLegacy(
			@RequestParam(value = "q", required = false) String q,
			@RequestParam(value = "keyword", required = false) String keyword) {
		String raw = firstNonBlankTrimmed(q, keyword);
		UriComponentsBuilder b = UriComponentsBuilder.fromPath("/books");
		if (!raw.isEmpty()) {
			b.queryParam("q", raw);
		}
		return "redirect:" + b.encode(StandardCharsets.UTF_8).build().toUriString();
	}

	private static String firstNonBlankTrimmed(String a, String b) {
		if (a != null && !a.isBlank()) {
			return a.trim();
		}
		if (b != null && !b.isBlank()) {
			return b.trim();
		}
		return "";
	}

	@GetMapping("/books/{bookId}")
	public String bookDetail(
			@PathVariable Integer bookId,
			Model model,
			@AuthenticationPrincipal LoginUserPrincipal user) {
		boolean reviewSaved = Boolean.TRUE.equals(model.asMap().get("reviewSaved"));
		String reviewError = (String) model.asMap().get("reviewError");
		String bookmarkMessage = (String) model.asMap().get("bookmarkMessage");
		String bookmarkError = (String) model.asMap().get("bookmarkError");
		String readingStatusMessage = (String) model.asMap().get("readingStatusMessage");
		String readingStatusError = (String) model.asMap().get("readingStatusError");
		String adminMessage = (String) model.asMap().get("adminMessage");
		String adminError = (String) model.asMap().get("adminError");
		return bookService.getBookById(bookId)
				.map(book -> {
					model.addAttribute("book", book);
					Integer userId = user != null ? user.getUserId() : null;
					model.addAttribute("currentUserId", userId);
					model.addAttribute("reviewSummary", reviewService.summaryForBook(bookId, userId));
					if (userId != null) {
						model.addAttribute("bookmarked", bookmarkService.isBookmarked(userId, bookId));
						model.addAttribute(
								"readingStatusCode",
								readingStatusService.getStatusCode(userId, bookId).orElse(ReadingStatusService.STATUS_CLEAR));
					} else {
						model.addAttribute("bookmarked", false);
						model.addAttribute("readingStatusCode", ReadingStatusService.STATUS_CLEAR);
					}
					model.addAttribute("reviewSaved", reviewSaved);
					model.addAttribute("reviewError", reviewError);
					model.addAttribute("bookmarkMessage", bookmarkMessage);
					model.addAttribute("bookmarkError", bookmarkError);
					model.addAttribute("readingStatusMessage", readingStatusMessage);
					model.addAttribute("readingStatusError", readingStatusError);
					model.addAttribute("adminMessage", adminMessage);
					model.addAttribute("adminError", adminError);
					return "book-detail";
				})
				.orElse("redirect:/books");
	}

	@PostMapping("/books/{bookId}/reviews")
	public String postReview(
			@PathVariable Integer bookId,
			@RequestParam("rating") double rating,
			@RequestParam(value = "comment", required = false) String comment,
			@RequestParam(value = "spoiler", defaultValue = "false") boolean spoiler,
			@AuthenticationPrincipal LoginUserPrincipal user,
			RedirectAttributes redirectAttributes) {
		if (bookService.getBookById(bookId).isEmpty()) {
			return "redirect:/books";
		}
		try {
			reviewService.saveOrUpdateReview(bookId, rating, comment, spoiler, user.getUserId());
			redirectAttributes.addFlashAttribute("reviewSaved", true);
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("reviewError", ex.getMessage());
		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("reviewError", "保存できませんでした。");
		}
		return "redirect:/books/" + bookId;
	}

	@PostMapping("/books/{bookId}/bookmarks")
	public String changeBookmark(
			@PathVariable Integer bookId,
			@RequestParam(value = "remove", defaultValue = "false") boolean remove,
			@AuthenticationPrincipal LoginUserPrincipal user,
			RedirectAttributes redirectAttributes) {
		if (bookService.getBookById(bookId).isEmpty()) {
			return "redirect:/books";
		}
		try {
			if (remove) {
				bookmarkService.removeBookmark(user.getUserId(), bookId);
				redirectAttributes.addFlashAttribute("bookmarkMessage", "ブックマークを解除しました。");
			} else {
				bookmarkService.addBookmark(user.getUserId(), bookId);
				redirectAttributes.addFlashAttribute("bookmarkMessage", "ブックマークに追加しました。");
			}
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("bookmarkError", ex.getMessage());
		}
		return "redirect:/books/" + bookId;
	}

	@PostMapping("/books/{bookId}/reading-status")
	public String updateReadingStatus(
			@PathVariable Integer bookId,
			@RequestParam("statusCode") int statusCode,
			@AuthenticationPrincipal LoginUserPrincipal user,
			RedirectAttributes redirectAttributes) {
		if (bookService.getBookById(bookId).isEmpty()) {
			return "redirect:/books";
		}
		try {
			readingStatusService.setStatus(user.getUserId(), bookId, statusCode);
			redirectAttributes.addFlashAttribute("readingStatusMessage", "読書ステータスを保存しました。");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("readingStatusError", ex.getMessage());
		}
		return "redirect:/books/" + bookId;
	}
}

