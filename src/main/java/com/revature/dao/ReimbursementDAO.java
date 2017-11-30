package com.revature.dao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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

	public BufferedInputStream getReceipt(Reimbursement reimbursement);

	public void sendReceipt(Integer id, BufferedOutputStream bos);
}
