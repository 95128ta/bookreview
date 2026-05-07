package com.bookreview.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class BookmarkId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "user_id", nullable = false)
	private Integer userId;

	@Column(name = "book_id", nullable = false)
	private Integer bookId;
}

