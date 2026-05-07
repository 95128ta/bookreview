package com.bookreview.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookreview.domain.Book;
import com.bookreview.repository.BookRepository;

@Service
public class BookService {

	private final BookRepository bookRepository;

	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	public List<Book> getRecentBooks() {
		return bookRepository.findTop5ByOrderByCreatedAtDesc();
	}

	public List<Book> getRecommendedBooks() {
		return bookRepository.findRecommendedBooksRandom();
	}

	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}

	/**
	 * タイトル・著者・出版社・説明のいずれかにキーワードを含む書籍を検索する。
	 * {@code keyword} が空のときは全件（作成日降順ではない）{@link #getAllBooks()} と同じ並び。
	 */
	public List<Book> searchBooks(String keyword) {
		if (keyword == null) {
			return getAllBooks();
		}
		String trimmed = keyword.trim();
		if (trimmed.isEmpty()) {
			return getAllBooks();
		}
		return bookRepository.searchByKeyword(trimmed);
	}

	public Optional<Book> getBookById(Integer bookId) {
		return bookRepository.findById(bookId);
	}

	@Transactional
	public Book createBook(Book book) {
		book.setBookId(null);
		return bookRepository.save(book);
	}

	@Transactional
	public Book saveBook(Book book) {
		return bookRepository.save(book);
	}

	@Transactional
	public void deleteBook(Integer bookId) {
		if (!bookRepository.existsById(bookId)) {
			throw new IllegalArgumentException("book not found");
		}
		bookRepository.deleteById(bookId);
	}
}

