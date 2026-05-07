package com.bookreview.domain;

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
@Table(name = "reading_status")
@Getter
@Setter
@NoArgsConstructor
public class ReadingStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "status_id")
	private Integer statusId;

	@Column(name = "user_id", nullable = false)
	private Integer userId;

	@Column(name = "book_id", nullable = false)
	private Integer bookId;

	@Column(name = "status_code", nullable = false)
	private Integer statusCode;
}

