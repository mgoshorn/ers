package com.revature.dao;

import java.sql.SQLException;
import java.util.List;

import com.revature.beans.Reimbursement;
import com.revature.beans.User;

public interface ReimbursementDAO {
	public List<Reimbursement> getReimbursementsByUser(User user) throws SQLException;
	
	public Reimbursement getReimbursementByID(int id);
	
	public void save(Reimbursement reimbursement) throws SQLException;
	
	public List<Reimbursement> getPendingRequests();
}
