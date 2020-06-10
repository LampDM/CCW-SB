package com.ccw.demo.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.ccw.demo.interfaceService.IsolutionService;
import com.ccw.demo.interfaceService.ItaskService;
import com.ccw.demo.interfaceService.IuserService;
import com.ccw.demo.service.CompilerService;
import com.ccw.demo.model.Solution;
import com.ccw.demo.model.Task;
import com.ccw.demo.model.User;

@Controller
@RequestMapping
public class Regler {
	//TODO Add the current max task scores
	//TODO add some submitting system similar to the FRI one, tokens, intervals between submissions
	//TODO priority list or. your task will be ran in X mins there are currently Y tasks infront of you
	int somecounter = 0;

	@Autowired
	private CompilerService cs;

	@Autowired
	private ItaskService tservice;

	@Autowired
	private IuserService uservice;

	@Autowired
	private IsolutionService sservice;

	@GetMapping("/")
	public String list(HttpSession session, Model model, Principal principal) {
		List<Task> tasks = tservice.list();
		for(int k = 0;k<tasks.size();k++) {
			User usr = uservice.getUser(principal.getName());
			Solution sol = sservice.getSolution(usr, tasks.get(k));
			tasks.get(k).setScore(sol.getScore());
			tasks.get(k).setInfo(sol.getInfo());
		}
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
	public String solveGet(@PathVariable int id, Model model, Principal principal) {

		Optional<Task> task = tservice.listId(id);

		Task tsk = task.get();
		User usr = uservice.getUser(principal.getName());

		Solution sol = sservice.getSolution(usr, tsk);

		model.addAttribute("solution", sol);
		model.addAttribute("task", task);

		return "solve";
	}

	@PostMapping("/solve")
	public RedirectView solvePost(Solution s, Model model, Principal principal, RedirectAttributes redir) {

		Task tsk = s.getTsk();

		Solution prev = sservice.getSolution(s.getUsr(), tsk);

		prev.setAnswer(s.getAnswer());

		// TODO add more tests
		// TODO afterwards rework the lists
		// TODO handle infinite loops
		// TODO make a result submit system
		// TODO put the version on the server!

		ArrayList<String> cmsg_list = new ArrayList<String>();
		String cmsg_string = "";
		String url_result = "";
		String score = "Null";

		try {
			//TODO make it return actual scores
			cmsg_list = cs.start(tsk.getTests(), prev.getAnswer());
			
			if(cmsg_list.get(0).equals("ok")) {
				score = cmsg_list.get(cmsg_list.size()-1);
				cmsg_string = cmsg_list.toString();
				url_result = "feedback";

			}else {
				cmsg_string = cmsg_list.toString();
				url_result = "error";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			cmsg_string = "Internal error! Please check test cases or system condition! " + e.toString() + " errs: "
					+ errors.toString();
			url_result = "error";
		}

		RedirectView rv = new RedirectView("/solve/" + tsk.getId() + "?" + url_result, true);
		redir.addFlashAttribute("compiler_message", cmsg_string);

		prev.setScore(score);
		prev.setInfo(cmsg_list.toString());
		sservice.save(prev);
		return rv;
	}

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
		if (!uservice.existsUser(u.getUsername())) {
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
