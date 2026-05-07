package com.bookreview.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bookreview.repository.ReviewRepository;
import com.bookreview.web.dto.RankingEntry;

@Service
public class RankingService {

	private final ReviewRepository reviewRepository;

	public RankingService(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;
	}

	public List<RankingEntry> getBookRanking() {
		return reviewRepository.findBookRanking();
	}
}

