package com.bookreview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookreview.domain.Book;
import com.bookreview.domain.ReadingStatus;

public interface ReadingStatusRepository extends JpaRepository<ReadingStatus, Integer> {

	Optional<ReadingStatus> findByUserIdAndBookId(Integer userId, Integer bookId);

	long countByUserId(Integer userId);

	long countByUserIdAndStatusCode(Integer userId, Integer statusCode);

	@Query("""
			SELECT b FROM Book b
			JOIN ReadingStatus rs ON b.bookId = rs.bookId
			WHERE rs.userId = :userId AND rs.statusCode = :statusCode
			ORDER BY b.title
			""")
	List<Book> findBooksByUserAndStatusCode(@Param("userId") Integer userId, @Param("statusCode") Integer statusCode);
}

