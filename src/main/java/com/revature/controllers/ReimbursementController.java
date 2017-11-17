package com.revature.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.revature.beans.Reimbursement;
import com.revature.exceptions.AuthenticationException;
import com.revature.exceptions.ForbiddenException;
import com.revature.services.ReimbursementService;
import com.revature.services.UserService;

public class ReimbursementController {

	private static Logger log = Logger.getRootLogger();
	private static ReimbursementService service = new ReimbursementService();
	private static UserService userService = new UserService();
	
	public void delegateGet(HttpServletRequest request, HttpServletResponse response) {
		log.debug("Get request delegated to reimbursement controller");
		String actualURL = request.getRequestURI().substring(request.getContextPath().length() + "/reimbursement".length());
		
		
		
		if("/pending/".equals(actualURL) || "/pending".equals(actualURL)) {
			try {
				HttpSession session = request.getSession(false);
				
				//User not logged in to 
				if(session == null) {
					log.trace("Access forbidden: No session");
					throw new ForbiddenException();
				}
				
				String username = (String)session.getAttribute("username");
				String password = (String)session.getAttribute("password");
								
				List<Reimbursement> pendingReimbursements = service.getAllPending(username, password);
				
				
				ObjectMapper om = new ObjectMapper();
				ObjectWriter ow = om.writer().withDefaultPrettyPrinter();
				String json = ow.writeValueAsString(pendingReimbursements);
				PrintWriter writer = response.getWriter();
				writer.write(json);
				return;
			} catch(IOException e) {
				
			}
		
		}
	}

}
