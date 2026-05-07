package com.bookreview.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookreview.domain.Book;
import com.bookreview.domain.Bookmark;
import com.bookreview.domain.BookmarkId;
import com.bookreview.repository.BookRepository;
import com.bookreview.repository.BookmarkRepository;

@Service
public class BookmarkService {

	private final BookmarkRepository bookmarkRepository;
	private final BookRepository bookRepository;

	public BookmarkService(BookmarkRepository bookmarkRepository, BookRepository bookRepository) {
		this.bookmarkRepository = bookmarkRepository;
		this.bookRepository = bookRepository;
	}

	public boolean isBookmarked(Integer userId, Integer bookId) {
		return bookmarkRepository.countForUserAndBook(userId, bookId) > 0;
	}

	public List<Book> listBookmarkedBooks(Integer userId) {
		return bookmarkRepository.findBooksBookmarkedByUser(userId);
	}

	@Transactional
	public void addBookmark(Integer userId, Integer bookId) {
		if (!bookRepository.existsById(bookId)) {
			throw new IllegalArgumentException("book not found");
		}
		if (bookmarkRepository.countForUserAndBook(userId, bookId) > 0) {
			return;
		}
		BookmarkId id = new BookmarkId();
		id.setUserId(userId);
		id.setBookId(bookId);
		Bookmark bookmark = new Bookmark();
		bookmark.setId(id);
		bookmarkRepository.save(bookmark);
	}

	@Transactional
	public void removeBookmark(Integer userId, Integer bookId) {
		bookmarkRepository.findForUserAndBook(userId, bookId).ifPresent(bookmarkRepository::delete);
	}
}

