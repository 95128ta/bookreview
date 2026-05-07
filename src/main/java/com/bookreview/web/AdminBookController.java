package com.bookreview.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookreview.domain.Book;
import com.bookreview.security.LoginUserPrincipal;
import com.bookreview.service.BookService;
import com.bookreview.web.dto.BookForm;

import jakarta.validation.Valid;

@Controller
public class AdminBookController {

	private final BookService bookService;

	public AdminBookController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping("/admin/books")
	public String list(Model model) {
		model.addAttribute("books", bookService.getAllBooks());
		return "admin/books";
	}

	@GetMapping("/admin/books/new")
	public String newForm(Model model) {
		model.addAttribute("form", new BookForm());
		return "admin/book-form";
	}

	@PostMapping("/admin/books/new")
	public String create(
			@Valid @ModelAttribute("form") BookForm form,
			BindingResult bindingResult,
			@AuthenticationPrincipal LoginUserPrincipal user,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "admin/book-form";
		}
		Book book = form.toNewBook();
		book.setUserId(user.getUserId());
		Book saved = bookService.createBook(book);
		redirectAttributes.addFlashAttribute("adminMessage", "書籍を登録しました。");
		return "redirect:/books/" + saved.getBookId();
	}

	@GetMapping("/admin/books/{bookId}/edit")
	public String editForm(@PathVariable Integer bookId, Model model) {
		Book book = bookService.getBookById(bookId).orElseThrow(() -> new IllegalArgumentException("book not found"));
		model.addAttribute("form", BookForm.fromBook(book));
		return "admin/book-form";
	}

	@PostMapping("/admin/books/{bookId}/edit")
	public String update(
			@PathVariable Integer bookId,
			@Valid @ModelAttribute("form") BookForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "admin/book-form";
		}
		if (form.getBookId() == null || !form.getBookId().equals(bookId)) {
			redirectAttributes.addFlashAttribute("adminError", "書籍 ID が一致しません。");
			return "redirect:/admin/books";
		}
		Book book = bookService.getBookById(bookId).orElseThrow(() -> new IllegalArgumentException("book not found"));
		form.applyTo(book);
		bookService.saveBook(book);
		redirectAttributes.addFlashAttribute("adminMessage", "書籍を更新しました。");
		return "redirect:/books/" + bookId;
	}

	@PostMapping("/admin/books/{bookId}/delete")
	public String delete(@PathVariable Integer bookId, RedirectAttributes redirectAttributes) {
		try {
			bookService.deleteBook(bookId);
			redirectAttributes.addFlashAttribute("adminMessage", "書籍を削除しました。");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("adminError", ex.getMessage());
		}
		return "redirect:/admin/books";
	}
}

