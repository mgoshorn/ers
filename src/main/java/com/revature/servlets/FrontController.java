package com.revature.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.servlets.DefaultServlet;
import org.apache.log4j.Logger;

import com.revature.controllers.ReimbursementController;
import com.revature.controllers.UserController;
import com.revature.exceptions.ErsHTTPException;
import com.revature.exceptions.ResourceNotFoundException;

public class FrontController extends DefaultServlet {

	private static final long serialVersionUID = 6584705380694818057L;
	private static Logger log = Logger.getRootLogger();
	private ReimbursementController reimbursementController = new ReimbursementController();
	private UserController userController = new UserController();
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        resp.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS, DELETE");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
		super.service(req, resp);
	}
	
	/**
	 * Handles get requests to server. Uses URI string to attempt to find a routing, defined
	 * as an instance of {@link FrontControllerDelegates}
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try {
			String actualURI = request.getRequestURI().substring(request.getContextPath().length());
			log.trace("Full URI: " + request.getRequestURI());
			log.trace("URI: " + actualURI);
			
			//Get URI delegate
			FrontControllerDelegates delegate = FrontControllerDelegates.getDelegate(actualURI);
		
			//Route by delegate
			switch(delegate) {
				case STATIC: 
					super.doGet(request, response);
					break;
					
				case REIMBURSEMENT:
					reimbursementController.delegateGet(request, response);
					break;
					
				case USER:
					userController.delegateGet(request, response);
					break;
					
				case LOGOUT:
					FrontController.logout(request, response);
					break;
				
				case RESOURCE_NOT_FOUND:
				default:
					throw new ResourceNotFoundException();
			}
			
			
		} catch(ErsHTTPException e) {
			log.trace("HttpException: " + e.getStatusCode() + " - Forwarding to: " + e.getLocation());
			response.setStatus(e.getStatusCode());
			request.getRequestDispatcher(e.getLocation()).forward(request, response);
			//response.sendRedirect(e.getLocation());
		} catch(Exception e) {	
			log.trace(e);
			e.printStackTrace();
			response.setStatus(500);
			request.getRequestDispatcher("/static/error/500.html").forward(request, response);
		}	
	}

	private static void logout(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().invalidate();
		
		try {
			response.sendRedirect("/user/login");
		} catch(IOException e) {
			e.printStackTrace();
			log.warn(e.getMessage());
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		try {
			String actualURI = request.getRequestURI().substring(request.getContextPath().length());
			log.trace("Full URI: " + request.getRequestURI());
			log.trace("URI: " + actualURI);
			
			//Get URI delegate
			FrontControllerDelegates delegate = FrontControllerDelegates.getDelegate(actualURI);
			log.trace("Delegating as: " + delegate.toString());
			
			//Route by delegate
			switch(delegate) {
				case STATIC: 
					super.doPost(request, response);
					break;
					
				case REIMBURSEMENT:
					reimbursementController.delegatePost(request, response);
					break;
					
				case USER:
					userController.delegatePost(request, response);
					break;
				
				case RESOURCE_NOT_FOUND:
				default:
					throw new ResourceNotFoundException();
			}
			
			
		} catch(ErsHTTPException e) {
			log.trace("HttpException: " + e.getStatusCode() + " - Forwarding to: " + e.getLocation());
			response.setStatus(e.getStatusCode());
			request.getRequestDispatcher(e.getLocation()).forward(request, response);
			//response.sendRedirect(e.getLocation());
		} catch(Exception e) {	
			log.trace(e);
			e.printStackTrace();
			response.setStatus(500);
			request.getRequestDispatcher("/static/error/500.html").forward(request, response);
		}
		
		//super.doPost(request, response); 
	}
}
