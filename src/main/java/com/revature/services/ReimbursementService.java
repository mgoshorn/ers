package com.revature.services;

import java.util.List;

import org.apache.log4j.Logger;

import com.revature.beans.Reimbursement;
import com.revature.beans.Role;
import com.revature.beans.User;
import com.revature.dao.ReimbursementDAO;
import com.revature.dao.ReimbursementDAOJdbc;
import com.revature.dao.UsersDAO;
import com.revature.dao.UsersDAOJdbc;
import com.revature.exceptions.AuthenticationException;
import com.revature.exceptions.ErsHTTPException;
import com.revature.exceptions.ForbiddenException;

public class ReimbursementService {
	ReimbursementDAO dao = new ReimbursementDAOJdbc();
	UsersDAO userDao = new UsersDAOJdbc();
	private static Logger log = Logger.getRootLogger();
	
	public List<Reimbursement> getAllPending(String username, String password) throws ErsHTTPException {
		UserService userService = new UserService();		
		
		if(!userService.authenticate(username, password)) {
			log.trace("Client authentication failed.");
			throw new AuthenticationException();
		}
		
		User user = userDao.getUserByUsername(username);
		
		if(user.getRole() != Role.FINANCE_MANAGER) {
			throw new ForbiddenException();
		}
		
		return dao.getPendingRequests();
	}

}
