package com.revature.dao;

import java.sql.SQLException;
import java.util.List;

import com.revature.beans.Reimbursement;
import com.revature.beans.User;

public interface ReimbursementDAO {
	public List<Reimbursement> getReimbursementsByUser(User user);
	
	public Reimbursement getReimbursementByID(int id);
	
	public boolean save(Reimbursement reimbursement);
	
	public List<Reimbursement> getPendingRequests();

	List<Reimbursement> getPendingRequests(int userID);

	List<Reimbursement> getPendingRequestsByResolverID(int userID);
}
