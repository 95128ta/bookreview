package com.bookreview.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bookreview.service.RankingService;

@Controller
public class RankingController {

	private final RankingService rankingService;

	public RankingController(RankingService rankingService) {
		this.rankingService = rankingService;
	}

	@GetMapping("/ranking")
	public String ranking(Model model) {
		model.addAttribute("ranking", rankingService.getBookRanking());
		return "ranking";
	}
}

