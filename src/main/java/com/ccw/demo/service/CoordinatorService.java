package com.ccw.demo.service;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.RedirectView;

import com.ccw.demo.interfaceService.IsolutionService;
import com.ccw.demo.model.Solution;
import com.ccw.demo.model.Task;

@Component
public class CoordinatorService {

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	Queue<Solution> solutions_queue = new LinkedList<Solution>();
	
	@Autowired
	private CompilerService cs;
	
	@Autowired
	private IsolutionService sservice;

	public CoordinatorService() {
		process_solutions();
	}

	public void process_solutions() {
		final Runnable saver = new Runnable() {
			public void run() {
				System.out.println("Processing solutions in queue!");
				while (!solutions_queue.isEmpty()) {
					// TODO do it in a seperate thread?
					processSolution(solutions_queue.poll());
				}
			}
		};
		final ScheduledFuture<?> saverHandle = scheduler.scheduleAtFixedRate(saver, 10, 10, SECONDS);
		scheduler.schedule(new Runnable() {
			public void run() {
				saverHandle.cancel(true);
			}
		}, 60 * 60, SECONDS);
	}

	public void sendSolution(Solution s) {
		solutions_queue.add(s);
	}

	public void processSolution(Solution s) {
		Task tsk = s.getTsk();

		// TODO add more tests
		// TODO afterwards rework the lists
		// TODO handle infinite loops
		// TODO give execution a max time span in configuration
		// TODO allow only so many solvings per minute
		
		//TODO user settings control panel, how many tokens do they have, time between token usage etc.

		ArrayList<String> cmsg_list = new ArrayList<String>();
		String cmsg_string = "";
		String url_result = "";
		String score = "None";

		Date currentDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-YYYY");
		String c_date = dateFormat.format(currentDate);

		try {
			//add max allowed time
			cmsg_list = cs.start(tsk.getTests(), tsk.getMaxtime(), s.getAnswer());

			if (cmsg_list.get(0).equals("ok")) {
				score = cmsg_list.get(cmsg_list.size() - 1);
				cmsg_string = cmsg_list.toString();
				url_result = "feedback";

			} else {
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

		s.setScore(score);
		s.setInfo(cmsg_list.toString());
		s.setDate(c_date);
		sservice.save(s);
	}

}
