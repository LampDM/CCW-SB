package com.ccw.demo.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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

		String cmsg = "";
		String url_result = "";
		String score = "insert score here";

		try {
			cmsg = cs.start(tsk.getTests(), prev.getAnswer());
			if (cmsg.startsWith(" ")) {
				url_result = "feedback";
			} else {
				url_result = "error";
			}

		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			cmsg = "Internal error! Please check test cases or system condition! " + e.toString() + " errs: "
					+ errors.toString();
			url_result = "error";
		}

		RedirectView rv = new RedirectView("/solve/" + tsk.getId() + "?" + url_result, true);
		redir.addFlashAttribute("compiler_message", cmsg);

		prev.setScore(score);
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

	@GetMapping("/get_json")
	@ResponseBody
	public HashMap viewJson() {
		somecounter++;
		System.out.println(somecounter);
		HashMap<String, Integer> map = new HashMap<>();
		map.put("Internal var", somecounter);

		return map;
	}

	@GetMapping("/ajaxform")
	public String ajaxForm() {
		return "/ajaxform";
	}

}
