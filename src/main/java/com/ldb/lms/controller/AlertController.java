package com.ldb.lms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AlertController {

	@GetMapping("/alert")
	public String callAlert(@RequestParam("url") String url,
			@RequestParam("msg") String msg, Model model) {
		model.addAttribute("url", url);
		model.addAttribute("msg", msg);
		return "alert";
	}
}
