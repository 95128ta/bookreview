package com.bookreview.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookreview.domain.AppUser;
import com.bookreview.domain.Review;
import com.bookreview.repository.AppUserRepository;
import com.bookreview.repository.BookRepository;
import com.bookreview.repository.ReviewRepository;

@Service
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final BookRepository bookRepository;
	private final AppUserRepository appUserRepository;

	public ReviewService(
			ReviewRepository reviewRepository,
			BookRepository bookRepository,
			AppUserRepository appUserRepository) {
		this.reviewRepository = reviewRepository;
		this.bookRepository = bookRepository;
		this.appUserRepository = appUserRepository;
	}

	public List<ReviewWithUserName> listReviewsForBook(Integer bookId) {
		List<Review> reviews = reviewRepository.findByBookIdOrderByCreatedAtDesc(bookId);
		if (reviews.isEmpty()) {
			return List.of();
		}
		Set<Integer> userIds = reviews.stream().map(Review::getUserId).collect(Collectors.toSet());
		Map<Integer, String> namesById = appUserRepository.findAllById(userIds).stream()
				.collect(Collectors.toMap(AppUser::getUserId, AppUser::getUserName));
		return reviews.stream()
				.map(r -> new ReviewWithUserName(r, namesById.getOrDefault(r.getUserId(), "不明")))
				.toList();
	}

	public BookReviewSummary summaryForBook(Integer bookId, Integer currentUserId) {
		List<ReviewWithUserName> rows = listReviewsForBook(bookId);
		double average = rows.isEmpty() ? 0
				: rows.stream().mapToDouble(r -> r.review().getRating()).average().orElse(0);
		boolean hasCurrentUserReview = currentUserId != null
				&& reviewRepository.existsByUserIdAndBookId(currentUserId, bookId);
		return new BookReviewSummary(rows, average, rows.size(), hasCurrentUserReview);
	}

	@Transactional
	public void saveOrUpdateReview(Integer bookId, double rating, String comment, boolean spoiler, Integer userId) {
		Objects.requireNonNull(bookId, "bookId");
		Objects.requireNonNull(userId, "userId");
		if (!bookRepository.existsById(bookId)) {
			throw new IllegalArgumentException("book not found: " + bookId);
		}
		if (rating < 0.5 || rating > 5.0) {
			throw new IllegalArgumentException("rating out of range");
		}
		appUserRepository.findById(userId).orElseThrow(() -> new IllegalStateException("user missing"));

		String trimmed = comment == null ? "" : comment.trim();
		if (trimmed.length() > 4000) {
			throw new IllegalArgumentException("comment too long");
		}

		int spoilerFlag = spoiler ? 1 : 0;
		reviewRepository.findByBookIdAndUserId(bookId, userId).ifPresentOrElse(existing -> {
			existing.setRating(rating);
			existing.setComment(trimmed.isEmpty() ? null : trimmed);
			existing.setSpoiler(spoilerFlag);
			reviewRepository.save(existing);
		}, () -> {
			Review r = new Review();
			r.setUserId(userId);
			r.setBookId(bookId);
			r.setRating(rating);
			r.setComment(trimmed.isEmpty() ? null : trimmed);
			r.setSpoiler(spoilerFlag);
			reviewRepository.save(r);
		});
	}

	public record ReviewWithUserName(Review review, String userName) {
	}

	public record BookReviewSummary(
			List<ReviewWithUserName> rows,
			double averageRating,
			long totalCount,
			boolean currentUserHasReview) {
	}

	@Transactional
	public void deleteReviewForAdmin(Integer bookId, Integer reviewId) {
		Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("review not found"));
		if (!review.getBookId().equals(bookId)) {
			throw new IllegalArgumentException("review does not belong to this book");
		}
		reviewRepository.delete(review);
	}
}

