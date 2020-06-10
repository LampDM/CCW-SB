package com.ccw.demo.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
			List<Solution> sols = sservice.getSolutions(usr, tasks.get(k));
			Solution sol = sols.get(sols.size()-1);
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

		List<Solution> sols = sservice.getSolutions(usr, tsk);
		
		String[][] convertable_sols = new String[sols.size()][2];
		for(int k = 0;k<sols.size();k++) {
			convertable_sols[k][0] = String.valueOf(sols.get(k).getId());
			convertable_sols[k][1] = sols.get(k).getAnswer();
		}
		model.addAttribute("solutions", sols);
		model.addAttribute("somearraylist", convertable_sols);
		model.addAttribute("solution", sols.get(sols.size()-1));
		model.addAttribute("task", task);
		return "solve";
	}
	
//	@GetMapping("/switch/{id}")
//	public String swithSolution(@PathVariable int id, Model model, Principal principal) {
//		Optional<Solution> sol = sservice.listId(id);
//		Solution s = sol.get();
//		Task task = s.getTsk();
//		System.out.println("name of task");
//		System.out.println(task.getName());
//		User usr = uservice.getUser(principal.getName());
//		
//		List<Solution> sols = sservice.getSolutions(usr, task);
//
//		model.addAttribute("solutions", sols);
//		model.addAttribute("solution", s);
//		model.addAttribute("task", task);
//		
//		return "solve";
//	}

	@PostMapping("/solve")
	public RedirectView solvePost(Solution s, Model model, Principal principal, RedirectAttributes redir) {

		Task tsk = s.getTsk();

		// TODO add more tests
		// TODO afterwards rework the lists
		// TODO handle infinite loops
		// TODO give execution a max time span in configuration
		// TODO allow only so many solvings per minute

		ArrayList<String> cmsg_list = new ArrayList<String>();
		String cmsg_string = "";
		String url_result = "";
		String score = "None";
		
		Date currentDate = new Date ();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-YYYY");
		String c_date = dateFormat.format(currentDate);
		
		try {
			cmsg_list = cs.start(tsk.getTests(), s.getAnswer());
			
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

		s.setScore(score);
		s.setInfo(cmsg_list.toString());
		s.setDate(c_date);
		sservice.save(s);
		return rv;
	}

	@GetMapping("/delete/{id}")
	public String deleteTask(Model model, @PathVariable int id) {
		tservice.delete(id);
		return "redirect:/";
	}
	
	@GetMapping("/deleteSolution/{id}")
	public ModelAndView deleteSolution(Model model, @PathVariable int id) {
		Optional<Solution> sol = sservice.listId(id);
		Solution s = sol.get();
		sservice.delete(id);
		return new ModelAndView("redirect:" + "/solve/"+s.getTsk().getId());
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
