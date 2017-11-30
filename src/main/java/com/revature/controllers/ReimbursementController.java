package com.revature.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.revature.beans.Reimbursement;
import com.revature.beans.User;
import com.revature.controllers.routers.ReimbursementRoute;
import com.revature.exceptions.AuthenticationException;
import com.revature.exceptions.ForbiddenException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.input.objects.Credentials;
import com.revature.input.objects.ReimbursementRequest;
import com.revature.services.ReimbursementService;
import com.revature.services.UserService;

public class ReimbursementController {

	private static Logger log = Logger.getRootLogger();
	private static ReimbursementService service = new ReimbursementService();
	
	public void delegateGet(HttpServletRequest request, HttpServletResponse response) {
		log.debug("Get request delegated to reimbursement controller");
		String actualURL = request.getRequestURI().substring(request.getContextPath().length() + "/reimbursement".length());
		
		ReimbursementRoute route = ReimbursementRoute.getDelegate(actualURL);
		log.trace("Route: " + route.name());
		Credentials credentials = getCredentials(request);
		if(credentials == null) throw new ForbiddenException();
		try {
			switch(route) {
				case PENDING:
					handleGetPending(request, response, credentials);
					break;
					
				case HISTORY:
					handleHistoryRequest(request, response, credentials);			
					break;
					
				case RECEIPT:
					handleGetReceipt(request, response, actualURL, credentials);
					break;
					
				case RESOURCE_NOT_FOUND:
				//Following have no GET operation
				case DENY:
				case IMAGE:
				case APPROVE:
				case CREATE:
					throw new ResourceNotFoundException();
			}
		} catch(IOException e) {
			e.printStackTrace();
			log.error(e.toString());
			
		}
		
		//throw new ResourceNotFoundException();
		
	}

	/**
	 * Handles requests for getting a users reimbursement request history
	 * @param request - HTTP request
	 * @param response - HTTP response
	 * @param credentials - Authentication credentials from session
	 * @throws IOException - On Jackson parse failure
	 */
	private void handleHistoryRequest(HttpServletRequest request, HttpServletResponse response, Credentials credentials) throws IOException {
		List<Reimbursement> rList = service.getUserReimbursements(credentials);
		ObjectMapper om = new ObjectMapper();
		ObjectWriter ow = om.writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(rList);
		PrintWriter writer = response.getWriter();
		writer.write(json);
		return;
	}

	/**
	 * Gets Credentials from user request. 
	 * @param request - HTTP request
	 * @return Credentials of user or null if session does not contain credentials
	 */
	private Credentials getCredentials(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if(session == null) return null;
		Credentials credentials = new Credentials((String)session.getAttribute("username"), (String)session.getAttribute("password"));
		return credentials;
		
	}

	/**
	 * Delegate for post Reimbursement post requests
	 * @param request 
	 * @param response
	 */
	public void delegatePost(HttpServletRequest request, HttpServletResponse response) {
		log.debug("Get request delegated to reimbursement controller");
		String actualURL = request.getRequestURI().substring(request.getContextPath().length() + "/reimbursement".length());
		ReimbursementRoute route = ReimbursementRoute.getDelegate(actualURL);
		
		log.trace(actualURL + " delegating to: " + route.name());
		
		Credentials credentials = getCredentials(request);
		if(credentials == null) throw new ForbiddenException();
		try {
			switch(route) {
			case PENDING:
				handleGetPending(request, response, credentials);
				break;
				
			case CREATE:
				handleCreateRequest(request, response, credentials);
				break;
				
			case IMAGE:
				handleUploadImage(request, response, credentials);
				break;
				
			case HISTORY:
				handleHistoryRequest(request, response, credentials);			
				break;
				
			case DENY:
				handleResolveStatus(request, response, actualURL, credentials, false);
				break;
				
			case APPROVE:
				handleResolveStatus(request, response, actualURL, credentials, true);				
				break;
				
			case RECEIPT:
				handleGetReceipt(request, response, actualURL, credentials);
				break;
				
			case RESOURCE_NOT_FOUND:
				throw new ResourceNotFoundException();
			}
		} catch(IOException e) {
			e.printStackTrace();
			log.error(e.toString());
			
		}		
	}

	private void handleUploadImage(HttpServletRequest request, HttpServletResponse response, Credentials credentials) {
		User user = new UserService().getUserByCredentials(credentials);
		if(user == null) {
			throw new AuthenticationException();
		}
		
		try {
			InputStream is=request.getInputStream();
			OutputStream os=response.getOutputStream();
			byte[] buf = new byte[1000];
			response.setContentType("img/jpg");
			response.setHeader("Content-Disposition", "filename=receipt_" + 53 + ".jpg");
			for (int nChunk = is.read(buf); nChunk!=-1; nChunk = is.read(buf))
			{
			    os.write(buf, 0, nChunk);
			} 
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles requests for receipt images
	 * @param request - http request
	 * @param response - http response
	 * @param actualURL - relevant URI data
	 * @param credentials - User credentials
	 */
	private void handleGetReceipt(HttpServletRequest request, HttpServletResponse response, String actualURL,
			Credentials credentials) {
		
		//Get receipt index from url
		if(actualURL.charAt(actualURL.length() - 1) == '/') actualURL = actualURL.substring(0, actualURL.length() - 1);
		int idIndex = actualURL.lastIndexOf('/');
		Integer id = null;

		log.trace("Supposed id: " + actualURL.substring(idIndex+1));
		
		//Try to cast value to integer
		try {
			id = Integer.valueOf(actualURL.substring(idIndex+1));
		} catch(NumberFormatException e) {
			//if it can't be cast, then the url is malformed, throw 404
			throw new ResourceNotFoundException();
		}
		
//		BufferedInputStream bis = service.getReceipt(credentials, id);
		response.setContentType("img/jpg");
		response.setHeader("Content-Disposition", "filename=receipt_" + id + ".jpg");

		try(BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {
			
			service.sendReceipt(credentials, id, bos);
			
		} catch(IOException e) {
			e.printStackTrace();
		}
//		response.setContentLength(bytes.length);
//
//		try(OutputStream os = response.getOutputStream()) {
//		   os.write(bytes , 0, bytes.length);
//		} catch (IOException e) {
//		   e.printStackTrace();
//		}
	}

	private void handleResolveStatus(HttpServletRequest request, HttpServletResponse response, String url,  Credentials credentials,
			boolean resolution) {
		String idString = url.replace("/", "").replace("deny", "").replace("approve", "");
		Integer id = null;
		try {
			id = Integer.parseInt(idString);
			service.resolveStatus(credentials, id, resolution);
		} catch(NumberFormatException e) {
			log.error("Could not parse reimbursement id: " + idString);
		}		
	}
	
	private void handleCreateRequest(HttpServletRequest request, HttpServletResponse response,
			Credentials credentials) throws IOException {
		
		ReimbursementRequest reimbRequest;
		String json = request.getReader().lines().reduce( (acc, cur) -> acc+cur).get();
		log.trace("json received = " + json);
		ObjectMapper om = new ObjectMapper();
		reimbRequest = om.readValue(json, ReimbursementRequest.class);

		
		service.createRequest(credentials, reimbRequest);
		
		
	}
	
	private void handleGetPending(HttpServletRequest request, HttpServletResponse response
					, Credentials credentials) throws IOException {
		List<Reimbursement> pending = service.getAllPending(credentials);
		
		if(pending == null) {
			throw new ForbiddenException();
		}
		
		ObjectMapper om = new ObjectMapper();
		ObjectWriter ow = om.writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(pending);
		PrintWriter writer = response.getWriter();
		writer.write(json);
		return;

		
		
	}
}
