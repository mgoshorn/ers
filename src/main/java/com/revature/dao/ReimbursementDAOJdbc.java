package com.revature.dao;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.revature.beans.Reimbursement;
import com.revature.beans.ReimbursementStatus;
import com.revature.beans.ReimbursementType;
import com.revature.beans.User;
import com.revature.util.ConnectionUtil;

public class ReimbursementDAOJdbc extends AbstractReimbursementDAO {
	
	private static ConnectionUtil connectionUtil = ConnectionUtil.getConnectionUtil();
	private static Logger log = Logger.getRootLogger();
	
	public void save(Reimbursement reimbursement) throws SQLException {
		if(reimbursement.getId() == null) {
			savenew(reimbursement);
		} else {
			update(reimbursement);
		}
	}

	private void update(Reimbursement reimbursement) throws SQLException {
		String query = "UPDATE reimbursements SET"
				+ "reimb_amount = ?, "
				+ "reimb_submitted = ?, "
				+ "reimb_resolved = ?, "
				+ "reimb_description = ?, "
				+ "reimb_author = ?, "
				+ "reimb_resolver = ?, "
				+ "reimb_status_id = ?, "
				+ "reimb_type_id = ? "
				+ "WHERE id = ?";
		
		Connection conn = ConnectionUtil.getConnection();
		PreparedStatement preparedStatement = conn.prepareStatement(query);
		
		preparedStatement.setBigDecimal(1, reimbursement.getAmount());
		preparedStatement.setTimestamp(2, Timestamp.valueOf(reimbursement.getSubmitted()));
		preparedStatement.setTimestamp(3, Timestamp.valueOf(reimbursement.getResolved()));
		preparedStatement.setString(4, reimbursement.getDescription());
		preparedStatement.setInt(5, reimbursement.getAuthor().getId());
		preparedStatement.setInt(6, reimbursement.getResolver().getId());
		preparedStatement.setInt(7, reimbursement.getStatus().getValue());
		preparedStatement.setInt(8, reimbursement.getType().getValue());
		preparedStatement.setInt(9, reimbursement.getId());
		
		
	}

	private void savenew(Reimbursement reimbursement) throws SQLException {
		String query = "INSERT INTO reimbursements (reimb_amount, "
				+ "reimb_submitted, reimb_resolved, reimb_description, reimb_receipt, "
				+ "reimb_author, reimb_resolver, reimb_status_id, reimb_type_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		//TODO check if this is ideal given employees are disbursed over the country
		ZoneId zone = ZoneId.systemDefault();
		
		Connection conn = ConnectionUtil.getConnection();
		PreparedStatement preparedStatement = conn.prepareStatement(query);
		
		
		preparedStatement.setBigDecimal(1, reimbursement.getAmount());
		preparedStatement.setLong(2, reimbursement.getSubmitted().atZone(zone).toEpochSecond());
		preparedStatement.setLong(3, reimbursement.getResolved().atZone(zone).toEpochSecond());
	}

	@Override
	public List<Reimbursement> getReimbursementsByUser(User user) throws SQLException {
		String query = "SELECT * FROM reimbursements WHERE reimb_author = ?";
		List<Reimbursement> list = new ArrayList<>();
		
		Connection conn = ConnectionUtil.getConnection();
		PreparedStatement preparedStatement = conn.prepareStatement(query);
		preparedStatement.setInt(1, user.getId());
		
		ResultSet results = preparedStatement.executeQuery();
		
		while(results.next()) {
			Reimbursement reimbursement = extractReimbursement(results);
			list.add(reimbursement);
		}
		
		return list;
	}

	/**
	 * Helper method for retrieving Reimbursement data from a 
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	private Reimbursement extractReimbursement(ResultSet results) throws SQLException {
		UsersDAO dao = new UsersDAOJdbc();
				
		int id = results.getInt("REIMB_ID");
		BigDecimal amount = results.getBigDecimal("REIMB_AMOUNT");
		LocalDateTime submitDate= results.getTimestamp("REIMB_SUBMITTED").toLocalDateTime();
		LocalDateTime resolutionDate = results.getTimestamp("REIMB_RESOLVED").toLocalDateTime();
		String description = results.getString("REIMB_DESCRIPTION");
		//Blob receiptImage = results.getBlob("REIMB_RECEIPT");
		User authorID = dao.getUserByID(results.getInt("REIMB_AUTHOR"));
		User resolverID = dao.getUserByID(results.getInt("REIMB_RESOLVER"));
		ReimbursementStatus status = ReimbursementStatus.getByValue(results.getInt("REIMB_STATUS_ID"));
		ReimbursementType type = ReimbursementType.getByValue(results.getInt("REIMB_TYPE_ID"));
		
		Reimbursement reimbursement = new Reimbursement(amount, id, submitDate, resolutionDate, description, authorID, resolverID, status, type);
		return reimbursement;
	}

	@Override
	public Reimbursement getReimbursementByID(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Blob getImage(Reimbursement reimbursement) throws SQLException {
		String query = "SELECT reimb_receipt FROM reimbursements WHERE id = ?";
		
		//Don't bother querying the database if ID is null
		if(reimbursement.getId() == null) return null;
		
		Blob receiptImage = null;
		
		Connection conn = ConnectionUtil.getConnection();
		PreparedStatement preparedStatement = conn.prepareStatement(query);
		preparedStatement.setInt(1, reimbursement.getId());
		ResultSet results = preparedStatement.executeQuery();
		
		
		if(results.next()) {
			receiptImage = results.getBlob("REIMB_RECEIPT");
		}
		
		return receiptImage;
	}

	/**
	 * Fetches a list of pending Reimbursements from database and returns as a list of Reimbursement objects
	 * @return Reimbursements pending as List
	 */
	@Override
	public List<Reimbursement> getPendingRequests() {
		List<Reimbursement> reimbursements = new ArrayList<>();
		String query = "SELECT reimb_id, reimb_amount, reimb_Submitted, reimb_resolved, reimb_description, "
				+ "reimb_author, reimb_resolver, reimb_status_id, reimb_type_id FROM reimbursements "
				+ "WHERE reimb_status_id = ?";
		
		try(Connection conn = connectionUtil.getConnection()) {
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, ReimbursementStatus.PENDING.getValue());
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()) {
				reimbursements.add(extractReimbursement(resultSet));
			}
			
			return reimbursements;
			
		} catch(SQLException e) {
			log.error("Error getting pending requests");
			return null;
		}
	}


	
	
}
