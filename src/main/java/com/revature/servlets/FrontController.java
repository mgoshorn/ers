package com.revature.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlets.DefaultServlet;
import org.apache.log4j.Logger;

import com.revature.controllers.ReimbursementController;
import com.revature.exceptions.ErsHTTPException;

public class FrontController extends DefaultServlet {
	
	private static Logger log = Logger.getRootLogger();
	private ReimbursementController reimbursementController = new ReimbursementController();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try {
			String actualURI = request.getRequestURI().substring(request.getContextPath().length());
			log.trace("Full URI: " + request.getRequestURI());
			log.trace("URI: " + actualURI);

			if(actualURI.startsWith("/static")) {
				super.doGet(request, response);
			}
			
			if(actualURI.startsWith("/reimbursement")) {
				reimbursementController.delegateGet(request, response);
			}	
		} catch(ErsHTTPException e) {
			log.trace("HttpException: " + e.getStatusCode() + " - Forwarding to: " + e.getLocation());
			response.setStatus(e.getStatusCode());
			request.getRequestDispatcher(e.getLocation()).forward(request, response);
			//response.sendRedirect(e.getLocation());
		}
			
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		//TODO
		super.doPost(request, response); 
	}
}
