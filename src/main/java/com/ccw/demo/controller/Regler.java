package com.ccw.demo.controller;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ccw.demo.interfaceService.ItaskService;
import com.ccw.demo.interfaceService.IuserService;
import com.ccw.demo.model.Task;
import com.ccw.demo.model.User;

@Controller
@RequestMapping
public class Regler {

	@Autowired
	private ItaskService tservice;

	@Autowired
	private IuserService uservice;
	
	// @Autowired
	// private IsolutionService sservice;

	@GetMapping("/")
	public String list(Model model) {
		List<Task> tasks = tservice.list();
		model.addAttribute("tasks", tasks);
		return "index";
	}

	@GetMapping("/new")
	public String add(Model model) {
		model.addAttribute("task", new Task());
		return "form";
	}

	@PostMapping("/save")
	public String save(@Valid Task t, Model model) {
		tservice.save(t);
		return "redirect:/";
	}

	@GetMapping("/edit/{id}")
	public String edit(@PathVariable int id, Model model) {
		Optional<Task> task = tservice.listId(id);
		model.addAttribute("task", task);
		return "form";
	}

	@GetMapping("/solve/{id}")
	public String solve(@PathVariable int id, Model model) {
		Optional<Task> task = tservice.listId(id);
		model.addAttribute("task", task);
		return "solve";
	}

//	@PostMapping("/compile")
//	public String save(@Valid Solution s, Model model) {
//		tservice.save(s);
//		return "redirect:/";
//	}

	@GetMapping("/delete/{id}")
	public String delete(Model model, @PathVariable int id) {
		tservice.delete(id);
		return "redirect:/";
	}

	@GetMapping("/info")
	public String info() {
		return "info";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/register")
	public String register1() {
		return "register";
	}
	
	@PostMapping("/register")
	public ModelAndView register2(@Valid User u) {
		if (! uservice.existsUser(u.getUsername())) {
			uservice.save(u);
			return new ModelAndView("redirect:" + "/login?register");
		}
		return new ModelAndView("redirect:" + "/register?error");
	}
	
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "/access-denied";
    }

}
