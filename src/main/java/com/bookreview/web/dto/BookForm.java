package com.bookreview.web.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.bookreview.domain.Book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BookForm {

	private Integer bookId;

	@NotBlank
	@Size(max = 500)
	private String title;

	@NotBlank
	@Size(max = 255)
	private String author;

	@Size(max = 255)
	private String publisher;

	@Size(max = 10000)
	private String description;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate publishedDate;

	@Size(max = 500)
	private String imagePath;

	@Size(max = 2000)
	private String salesUrl;

	public static BookForm fromBook(Book b) {
		BookForm f = new BookForm();
		f.setBookId(b.getBookId());
		f.setTitle(b.getTitle());
		f.setAuthor(b.getAuthor());
		f.setPublisher(b.getPublisher());
		f.setDescription(b.getDescription());
		f.setPublishedDate(b.getPublishedDate());
		f.setImagePath(b.getImagePath());
		f.setSalesUrl(b.getSalesUrl());
		return f;
	}

	public Book toNewBook() {
		Book book = new Book();
		applyTo(book);
		return book;
	}

	public void applyTo(Book book) {
		book.setTitle(title.trim());
		book.setAuthor(author.trim());
		book.setPublisher(emptyToNull(publisher));
		book.setDescription(emptyToNull(description));
		book.setPublishedDate(publishedDate);
		book.setImagePath(emptyToNull(imagePath));
		book.setSalesUrl(emptyToNull(salesUrl));
	}

	private static String emptyToNull(String s) {
		if (s == null) {
			return null;
		}
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}

	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(LocalDate publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getSalesUrl() {
		return salesUrl;
	}

	public void setSalesUrl(String salesUrl) {
		this.salesUrl = salesUrl;
	}
}

