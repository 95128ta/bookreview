package com.bookreview.web.dto;

public class RankingEntry {

	private final Integer bookId;
	private final String title;
	private final Double averageRating;
	private final Long reviewCount;

	public RankingEntry(Integer bookId, String title, Double averageRating, Long reviewCount) {
		this.bookId = bookId;
		this.title = title;
		this.averageRating = averageRating;
		this.reviewCount = reviewCount;
	}

	public Integer getBookId() {
		return bookId;
	}

	public String getTitle() {
		return title;
	}

	public Double getAverageRating() {
		return averageRating;
	}

	public Long getReviewCount() {
		return reviewCount;
	}
}

