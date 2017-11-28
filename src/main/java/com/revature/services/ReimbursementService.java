package com.revature.services;

import java.util.List;

import org.apache.log4j.Logger;

import com.revature.beans.Reimbursement;
import com.revature.beans.ReimbursementStatus;
import com.revature.beans.Role;
import com.revature.beans.User;
import com.revature.dao.ReimbursementDAO;
import com.revature.dao.ReimbursementDAOJdbc;
import com.revature.dao.UsersDAO;
import com.revature.dao.UsersDAOJdbc;
import com.revature.exceptions.AuthenticationException;
import com.revature.exceptions.ErsHTTPException;
import com.revature.exceptions.ForbiddenException;
import com.revature.input.objects.Credentials;
import com.revature.input.objects.ReimbursementRequest;

public class ReimbursementService {
	ReimbursementDAO dao = new ReimbursementDAOJdbc();
	UsersDAO userDao = new UsersDAOJdbc();
	private static Logger log = Logger.getRootLogger();
	
	public List<Reimbursement> getAllPending(Credentials credentials) throws ErsHTTPException {
		UserService userService = new UserService();		
		User user = userService.getUserByCredentials(credentials);
		
		if(user == null) {
			log.trace("Client authentication failed.");
			throw new AuthenticationException();
		}
		
		if(user.getRole() != Role.FINANCE_MANAGER) {
			throw new ForbiddenException();
		}
		
		return dao.getPendingRequests();
	}

	
	public List<Reimbursement> getUserReimbursements(Credentials credentials) {
		log.trace("Requesting user reimbursement history");
		UserService userService = new UserService();
		
		User user = userService.getUserByCredentials(credentials);
		List<Reimbursement> reimbursements = dao.getReimbursementsByUser(user);
		log.trace("Reimbursements found for user: " + reimbursements.size());
		return reimbursements;
		
	}
	
	public boolean resolveStatus(Credentials credentials, int id, boolean approved) {
		
		ReimbursementStatus resolution = approved ? ReimbursementStatus.APPROVED : ReimbursementStatus.DENIED;
		
		//Authenticate
		UserService userService = new UserService();	
		User user = userService.getUserByCredentials(credentials);
		
		if(user == null) {
			log.warn("Unauthenticated user attempted request resolution. Provided reimb id: " + id);
			return false;
		}
		
		//Validate user is Finance Manager
		if(user.getRole() != Role.FINANCE_MANAGER) {
			log.warn("User without proper permissions attempted request resolution. User: " + user.getId() 
					+ ", provided reimbursement: " + id);
			return false;
		}
		
		//validate user reimbursement exists
		Reimbursement reimbursement = dao.getReimbursementByID(id);
		if(reimbursement == null) {
			log.error("No reimbursement with provided id: ");
			return false;
		}
		
		//validate user reimbursement is unresolved
		if(reimbursement.getStatus() != ReimbursementStatus.PENDING) {
			log.warn("Reimbursement " + reimbursement.getId() + " has already been resolved.");
			return false;
		}
		
		//validate user is not reimbursement requester
		if(reimbursement.getAuthor().getId() == user.getId()) {
			log.error("Finance manager attempted to approve own reimbursement. Reimbursement: " + reimbursement.getId());
			return false;
		}
				
		//Update status
		reimbursement.setStatus(resolution);
		boolean result = dao.save(reimbursement);
		
		//TODO e-mail request author alerting them of resolution
		
		return result;
		
	}


	public void createRequest(Credentials credentials, ReimbursementRequest reimbRequest) {
		UserService userService = new UserService();
		User user = userService.getUserByCredentials(credentials);
		if(user == null) {
			new ForbiddenException();
		}
		
		//Get reimbursement object from Request and user object
		Reimbursement reimbursement = new Reimbursement(user, reimbRequest);
		
		//Save reimbursement to database
		dao.save(reimbursement);
	}


	public byte[] getReceipt(Credentials credentials, Integer id) {
		//Authenticate user
		UserService us = new UserService();
		User user = us.getUserByCredentials(credentials);
		
		if(user == null) {
			throw new ForbiddenException();
		}
		
		//if a user is an employee and not the uploader, then deny access to file
		Reimbursement reimbursement = dao.getReimbursementByID(id);
		if(user.getRole() == Role.EMPLOYEE && user.getId() != reimbursement.getAuthor().getId() ) {
			throw new ForbiddenException();
		}
		
		return dao.getReceipt(reimbursement);		
	}
	
}
