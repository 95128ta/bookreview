package com.bookreview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookreview.domain.Book;
import com.bookreview.domain.Bookmark;
import com.bookreview.domain.BookmarkId;

public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkId> {

	@Query("SELECT COUNT(m) FROM Bookmark m WHERE m.id.userId = :userId AND m.id.bookId = :bookId")
	long countForUserAndBook(@Param("userId") Integer userId, @Param("bookId") Integer bookId);

	@Query("SELECT m FROM Bookmark m WHERE m.id.userId = :userId AND m.id.bookId = :bookId")
	Optional<Bookmark> findForUserAndBook(@Param("userId") Integer userId, @Param("bookId") Integer bookId);

	@Query("SELECT b FROM Book b JOIN Bookmark m ON b.bookId = m.id.bookId WHERE m.id.userId = :userId ORDER BY b.title")
	List<Book> findBooksBookmarkedByUser(@Param("userId") Integer userId);

	@Query("SELECT COUNT(m) FROM Bookmark m WHERE m.id.userId = :userId")
	long countByUserId(@Param("userId") Integer userId);
}

