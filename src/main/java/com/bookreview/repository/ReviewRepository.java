package com.bookreview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bookreview.domain.Review;
import com.bookreview.web.dto.RankingEntry;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

	List<Review> findByBookIdOrderByCreatedAtDesc(Integer bookId);

	Optional<Review> findByBookIdAndUserId(Integer bookId, Integer userId);

	boolean existsByUserIdAndBookId(Integer userId, Integer bookId);

	long countByUserId(Integer userId);

	List<Review> findByUserIdOrderByCreatedAtDesc(Integer userId);

	@Query("""
			SELECT NEW com.bookreview.web.dto.RankingEntry(b.bookId, b.title, AVG(r.rating), COUNT(r))
			FROM Review r, Book b
			WHERE r.bookId = b.bookId
			GROUP BY b.bookId, b.title
			ORDER BY AVG(r.rating) DESC, COUNT(r) DESC
			""")
	List<RankingEntry> findBookRanking();
}

