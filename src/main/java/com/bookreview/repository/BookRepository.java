package com.bookreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookreview.domain.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {

	List<Book> findTop5ByOrderByCreatedAtDesc();

	@Query(value = "SELECT * FROM \"book\" ORDER BY RAND() LIMIT 5", nativeQuery = true)
	List<Book> findRecommendedBooksRandom();

	@Query("""
			SELECT b FROM Book b WHERE
			LOWER(b.title) LIKE LOWER(CONCAT('%', :kw, '%'))
			OR LOWER(b.author) LIKE LOWER(CONCAT('%', :kw, '%'))
			OR (b.publisher IS NOT NULL AND LOWER(b.publisher) LIKE LOWER(CONCAT('%', :kw, '%')))
			OR (b.description IS NOT NULL AND LOWER(b.description) LIKE LOWER(CONCAT('%', :kw, '%')))
			ORDER BY b.createdAt DESC
			""")
	List<Book> searchByKeyword(@Param("kw") String kw);
}

