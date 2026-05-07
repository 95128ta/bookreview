package com.bookreview.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_id")
	private Integer bookId;

	@Column(name = "title", nullable = false, length = 500)
	private String title;

	@Column(name = "author", nullable = false, length = 255)
	private String author;

	@Column(name = "publisher", length = 255)
	private String publisher;

	@Column(name = "description", length = 10000)
	private String description;

	@Column(name = "published_date")
	private LocalDate publishedDate;

	@Column(name = "image_path", length = 500)
	private String imagePath;

	@Column(name = "sales_url", length = 2000)
	private String salesUrl;

	@Column(name = "user_id")
	private Integer userId;

	@Generated(GenerationTime.INSERT)
	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;
}

