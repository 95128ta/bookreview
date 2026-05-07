package com.bookreview.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookreview.domain.Book;
import com.bookreview.domain.ReadingStatus;
import com.bookreview.repository.BookRepository;
import com.bookreview.repository.ReadingStatusRepository;

@Service
public class ReadingStatusService {

	/** 未登録（あとで UI で「未設定」を選んだとき） */
	public static final int STATUS_CLEAR = 0;
	/** 読みたい */
	public static final int STATUS_WANT = 1;
	/** 読書中 */
	public static final int STATUS_READING = 2;
	/** 読了 */
	public static final int STATUS_FINISHED = 3;

	private final ReadingStatusRepository readingStatusRepository;
	private final BookRepository bookRepository;

	public ReadingStatusService(ReadingStatusRepository readingStatusRepository, BookRepository bookRepository) {
		this.readingStatusRepository = readingStatusRepository;
		this.bookRepository = bookRepository;
	}

	public Optional<Integer> getStatusCode(Integer userId, Integer bookId) {
		return readingStatusRepository.findByUserIdAndBookId(userId, bookId).map(ReadingStatus::getStatusCode);
	}

	public long countReadingBooks(Integer userId) {
		return readingStatusRepository.countByUserIdAndStatusCode(userId, STATUS_READING);
	}

	public List<Book> listReadingBooks(Integer userId) {
		return readingStatusRepository.findBooksByUserAndStatusCode(userId, STATUS_READING);
	}

	@Transactional
	public void setStatus(Integer userId, Integer bookId, int statusCode) {
		if (!bookRepository.existsById(bookId)) {
			throw new IllegalArgumentException("book not found");
		}
		if (statusCode == STATUS_CLEAR) {
			readingStatusRepository.findByUserIdAndBookId(userId, bookId).ifPresent(readingStatusRepository::delete);
			return;
		}
		if (statusCode < STATUS_WANT || statusCode > STATUS_FINISHED) {
			throw new IllegalArgumentException("invalid reading status");
		}
		Optional<ReadingStatus> existing = readingStatusRepository.findByUserIdAndBookId(userId, bookId);
		if (existing.isPresent()) {
			ReadingStatus row = existing.get();
			row.setStatusCode(statusCode);
			return;
		}
		ReadingStatus row = new ReadingStatus();
		row.setUserId(userId);
		row.setBookId(bookId);
		row.setStatusCode(statusCode);
		readingStatusRepository.save(row);
	}
}

