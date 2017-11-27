package com.revature.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.beans.User;
import com.revature.controllers.routers.UserRouter;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.input.objects.Credentials;
import com.revature.services.UserService;

public class UserController {

	private static Logger log = Logger.getRootLogger();
	UserService service = new UserService();
	
	public void delegateGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("Get request delegated to user controller");
		String actualURL = request.getRequestURI().substring(request.getContextPath().length() + "/user".length());
		
		UserRouter route = UserRouter.getDelegate(actualURL);
		
		switch(route) {
			case LOGIN:
				request.getRequestDispatcher("/static/user/login.html").forward(request, response);
				break;
			
			case RESOURCE_NOT_FOUND:
			case UPDATE_PASSWORD:
			case UPDATE_EMAIL:
			default:
				throw new ResourceNotFoundException();
		}
		
	}

	public void delegatePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String actualURI = this.getActualURI(request);
		
		UserRouter route = UserRouter.getDelegate(actualURI);
		
		switch(route) {
			case LOGIN:
				handleLogin(request, response);
				break;
				
			case UPDATE_PASSWORD:
				//TODO
				break;
			
			case UPDATE_EMAIL:
				//TODO
				break;
				
			case RESOURCE_NOT_FOUND:
			default:
				//TODO
				break;
		}
		
	}
	
	public String getActualURI(HttpServletRequest request) {
		return request.getRequestURI().substring(request.getContextPath().length() + "/user".length());
	}
	
	public boolean handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String json = request.getReader().lines().reduce( (acc, cur) -> acc+cur).get();
		
		log.trace("json received = " + json);
		ObjectMapper om = new ObjectMapper();
		Credentials credentials = om.readValue(json,  Credentials.class);
		log.trace("object created from json = " + credentials);
		
		//TODO remove this testing code
//		User newUser = service.createNewUser(credentials.getUsername(), "Tester", "Tester", credentials.getPassword(), "test@test.test");
//		if(newUser != null) log.trace("New user created.");
//		else log.trace("User creation unsuccessful");
		
		User user = service.getUserByCredentials(credentials);
		boolean success = user != null;
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String,String> jsonMap = new HashMap<>();
		
		String outputJson = "";
		
		if(success) {
			log.trace("User authentication successful");
			//Create session
			HttpSession session = request.getSession();
			session.setAttribute("username", credentials.getUsername());
			session.setAttribute("password", credentials.getPassword());
			
			//issue code and redirect
			response.setStatus(HttpServletResponse.SC_SEE_OTHER); //303
			outputJson = objectMapper.writeValueAsString(user);
		} else {
			log.trace("User authentication unsuccessful.");
			jsonMap.put("success", "false");
			response.setStatus(401);
			outputJson = objectMapper.writeValueAsString(jsonMap);
		}
		
		PrintWriter writer = response.getWriter();
		writer.write(outputJson);
		writer.flush();
		return success;
	}

}
