package com.revature.dao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.revature.beans.Reimbursement;
import com.revature.beans.ReimbursementStatus;
import com.revature.beans.ReimbursementType;
import com.revature.beans.User;
import com.revature.util.ConnectionUtil;

public class ReimbursementDAOJdbc extends AbstractReimbursementDAO {
	
	private static Logger log = Logger.getRootLogger();
	
	public boolean save(Reimbursement reimbursement) {
		if(reimbursement.getId() == null) {
			return savenew(reimbursement);
		} else {
			return update(reimbursement);
		}
	}
	
	private boolean update(Reimbursement reimbursement){
		String query = "UPDATE reimbursements SET "
				+ "reimb_amount = ?, "
				+ "reimb_submitted = ?, "
				+ "reimb_resolved = ?, "
				+ "reimb_description = ?, "
				+ "reimb_author = ?, "
				+ "reimb_resolver = ?, "
				+ "reimb_status_id = ?, "
				+ "reimb_type_id = ? "
				+ "WHERE reimb_id = ?";
		
		try(Connection conn = ConnectionUtil.getConnection()) {
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

			preparedStatement.execute();
			return true;
			
		} catch(SQLException e) {
			log.error("Reimbursement DAO update method failed: " + e.toString());
			e.printStackTrace();
			return false;
		}
		
	}

	private boolean savenew(Reimbursement reimbursement) {
		String query = "INSERT INTO reimbursements (reimb_amount, "
				+ "reimb_submitted, reimb_resolved, reimb_description, "
				+ "reimb_author, reimb_resolver, reimb_status_id, reimb_type_id, reimb_receipt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		
		try(Connection conn = ConnectionUtil.getConnection()) {
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			
			log.trace(reimbursement);
			Timestamp resolvedTs = reimbursement.getResolved() == null ? null : Timestamp.valueOf(reimbursement.getResolved());
			boolean resolverIdIsNull = reimbursement.getResolver() == null;
			Blob receipt = conn.createBlob();
			OutputStream blobWriter = receipt.setBinaryStream(1);
			blobWriter.write(reimbursement.getReceipt().getBytes());
			
			preparedStatement.setBigDecimal(1, reimbursement.getAmount());
			preparedStatement.setTimestamp(2, Timestamp.valueOf(reimbursement.getSubmitted()));
			preparedStatement.setTimestamp(3, resolvedTs);
			preparedStatement.setString(4, reimbursement.getDescription());
			preparedStatement.setInt(5, reimbursement.getAuthor().getId());
			if(resolverIdIsNull) {
				preparedStatement.setNull(6, Types.INTEGER);
			} else {
				preparedStatement.setInt(6, reimbursement.getResolver().getId());
			}
			preparedStatement.setInt(7, reimbursement.getStatus().getValue());
			preparedStatement.setInt(8, reimbursement.getType().getValue());
			
			preparedStatement.setBlob(9, receipt);
			preparedStatement.execute();
			
			return true;
		} catch(SQLException e) {
			e.printStackTrace();
			log.error("Reimbursement insert failed. " + e.toString());
			return false;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}

	@Override
	public List<Reimbursement> getReimbursementsByUser(User user){
		String query = "SELECT REIMB_ID, REIMB_AMOUNT, REIMB_SUBMITTED, REIMB_RESOLVED, "
				+ "REIMB_DESCRIPTION, REIMB_AUTHOR, REIMB_RESOLVER, REIMB_STATUS_ID, REIMB_TYPE_ID "
				+ "FROM reimbursements WHERE reimb_author = ? ORDER BY reimb_submitted DESC";
		List<Reimbursement> list = new ArrayList<>();
		
		try(Connection conn = ConnectionUtil.getConnection()) {
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, user.getId());
			
			ResultSet results = preparedStatement.executeQuery();
			
			list = extractReimbursements(results,conn);
//			while(results.next()) {
//				Reimbursement reimbursement = extractReimbursement(results, conn);
//				list.add(reimbursement);
//			}
			
			return list;
		} catch(SQLException e) {
			e.printStackTrace();
			log.error(e.toString());
			return null;
		}
		
	}

	/**
	 * Helper method for retrieving Reimbursement data from a 
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	private Reimbursement extractReimbursement(ResultSet results, Connection conn) throws SQLException {
		UsersDAO dao = new UsersDAOJdbc();
				
		int id = results.getInt("REIMB_ID");
		BigDecimal amount = results.getBigDecimal("REIMB_AMOUNT");
		
		Timestamp submit = results.getTimestamp("REIMB_SUBMITTED");
		LocalDateTime submitDate = results.wasNull() ? null : submit.toLocalDateTime();
		
		Timestamp resolve = results.getTimestamp("REIMB_RESOLVED");
		LocalDateTime resolutionDate = results.wasNull() ? null : resolve.toLocalDateTime();
		
		String description = results.getString("REIMB_DESCRIPTION");
		if(results.wasNull()) description = null;
		//Blob receiptImage = results.getBlob("REIMB_RECEIPT");
		
		
		User authorID = dao.getUserByID(results.getInt("REIMB_AUTHOR"), conn);
		User resolverID = dao.getUserByID(results.getInt("REIMB_RESOLVER"), conn);
		
		
		if(results.wasNull()) resolverID = null;
		ReimbursementStatus status = ReimbursementStatus.getByValue(results.getInt("REIMB_STATUS_ID"));
		ReimbursementType type = ReimbursementType.getByValue(results.getInt("REIMB_TYPE_ID"));
		
		Reimbursement reimbursement = new Reimbursement(amount, id, submitDate, resolutionDate, description, authorID, resolverID, status, type);
		return reimbursement;
	}
	
	private List<Reimbursement> extractReimbursements(ResultSet results, Connection conn) throws SQLException {
		UsersDAO dao = new UsersDAOJdbc();
		Map<Integer, User> userMap = new HashMap<>();
		userMap.put(null, null);
		List<Reimbursement> list = new ArrayList<>();
		
		while(results.next()) {
			int id = results.getInt("REIMB_ID");
			BigDecimal amount = results.getBigDecimal("REIMB_AMOUNT");
			
			Timestamp submit = results.getTimestamp("REIMB_SUBMITTED");
			LocalDateTime submitDate = results.wasNull() ? null : submit.toLocalDateTime();
			
			Timestamp resolve = results.getTimestamp("REIMB_RESOLVED");
			LocalDateTime resolutionDate = results.wasNull() ? null : resolve.toLocalDateTime();
			
			String description = results.getString("REIMB_DESCRIPTION");
			if(results.wasNull()) description = null;
			//Blob receiptImage = results.getBlob("REIMB_RECEIPT");
			
			Integer authorID = results.getInt("REIMB_AUTHOR");
			Integer resolverID = results.getInt("REIMB_RESOLVER");
			if(results.wasNull()) resolverID = null;
			
			User author = userMap.containsKey(authorID) ? userMap.get(authorID) : dao.getUserByID(authorID, conn);
			User resolver = userMap.containsKey(resolverID) ? userMap.get(resolverID) : dao.getUserByID(resolverID, conn);

			
			ReimbursementStatus status = ReimbursementStatus.getByValue(results.getInt("REIMB_STATUS_ID"));
			ReimbursementType type = ReimbursementType.getByValue(results.getInt("REIMB_TYPE_ID"));
			Reimbursement reimbursement = new Reimbursement(amount, id, submitDate, resolutionDate, description, author, resolver, status, type);
			list.add(reimbursement);
		}
		
		return list;
	}

	/**
	 * Attempts to find a reimbursement record from the database with provided ID
	 * @param id - provided ID
	 * @return reimbursement if found, else null
	 */
	@Override
	public Reimbursement getReimbursementByID(int id) {
		String query = "SELECT reimb_id, reimb_amount, reimb_Submitted, reimb_resolved, reimb_description, "
				+ "reimb_author, reimb_resolver, reimb_status_id, reimb_type_id FROM reimbursements "
				+ "WHERE reimb_id = ?";
		
		try(Connection conn = ConnectionUtil.getConnection()) {
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, id);
			ResultSet results = preparedStatement.executeQuery();
			
			if(results.next()) {
				return extractReimbursement(results, conn);
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
			log.error(e.toString());
		}
		return null;
	}

	@Override
	public Blob getImage(Reimbursement reimbursement) throws SQLException {
		String query = "SELECT reimb_receipt FROM reimbursements WHERE id = ?";
		
		//Don't bother querying the database if ID is null
		if(reimbursement.getId() == null) return null;
		
		Blob receiptImage = null;
		
		try(Connection conn = ConnectionUtil.getConnection()) {
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, reimbursement.getId());
			ResultSet results = preparedStatement.executeQuery();
			
			
			if(results.next()) {
				receiptImage = results.getBlob("REIMB_RECEIPT");
			}
			
			return receiptImage;
		}
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
				+ "WHERE reimb_status_id = ? ORDER BY reimb_submitted DESC";
		
		try(Connection conn = ConnectionUtil.getConnection()) {
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, ReimbursementStatus.PENDING.getValue());
			ResultSet resultSet = preparedStatement.executeQuery();
			
			reimbursements = extractReimbursements(resultSet, conn);	
			
//			while(resultSet.next()) {
//				reimbursements.add(extractReimbursement(resultSet, conn));
//			}
			
			return reimbursements;
			
		} catch(SQLException e) {
			log.error("Error getting pending requests");
			return null;
		}
	}

	/**
	 * Fetches a list of pending Reimbursements from database and returns as a list of Reimbursement objects
	 * @return Reimbursements pending as List
	 */
	@Override
	public List<Reimbursement> getPendingRequests(int userID) {
		List<Reimbursement> reimbursements = new ArrayList<>();
		String query = "SELECT reimb_id, reimb_amount, reimb_Submitted, reimb_resolved, reimb_description, "
				+ "reimb_author, reimb_resolver, reimb_status_id, reimb_type_id FROM reimbursements "
				+ "WHERE reimb_status_id = ? AND reimb_author != ? ORDER BY reimb_submitted DESC";
		
		try(Connection conn = ConnectionUtil.getConnection()) {
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, ReimbursementStatus.PENDING.getValue());
			preparedStatement.setInt(2, userID);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()) {
				reimbursements.add(extractReimbursement(resultSet, conn));
			}
			
			return reimbursements;
			
		} catch(SQLException e) {
			log.error("Error getting pending requests");
			return null;
		}
	}
	
	@Override
	public List<Reimbursement> getPendingRequestsByResolverID(int userID) {
		List<Reimbursement> reimbursements = new ArrayList<>();
		String query = "SELECT reimb_id, reimb_amount, reimb_Submitted, reimb_resolved, reimb_description, "
				+ "reimb_author, reimb_resolver, reimb_status_id, reimb_type_id FROM reimbursements "
				+ "reimb_resolver = ? ORDER BY reimb_submitted DESC";
		
		try(Connection conn = ConnectionUtil.getConnection()) {
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, userID);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()) {
				reimbursements.add(extractReimbursement(resultSet, conn));
			}
			
			return reimbursements;
			
		} catch(SQLException e) {
			log.error("Error getting resolver's resolved requests");
			return null;
		}
	}

	@Override
	public BufferedInputStream getReceipt(Reimbursement reimbursement) {
		String query = "SELECT reimb_receipt FROM reimbursements WHERE reimb_id = ?";
		
		try(Connection conn = ConnectionUtil.getConnection()) {
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, reimbursement.getId());
			ResultSet resultSet = preparedStatement.executeQuery();
			
			/*
			byte[] bytes = {};
			if(resultSet.next()) {
				Blob imgBlob = resultSet.getBlob("REIMB_RECEIPT");
				bytes = imgBlob.getBytes(1, (int)imgBlob.length());
			} 
			return bytes;*/
			
			if(resultSet.next()) {
				return new BufferedInputStream(resultSet.getBinaryStream("REIMB_RECEIPT"));
			}
			
			return null;
			
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void sendReceipt(Integer id, BufferedOutputStream bos) {
		String query = "SELECT reimb_receipt FROM reimbursements WHERE reimb_id = ?";
		
		try(Connection conn = ConnectionUtil.getConnection()) {
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()) {
				BufferedInputStream bis = new BufferedInputStream(resultSet.getBinaryStream("REIMB_RECEIPT"));
				byte[] buffer = new byte[1024];
				for (int length = 0; (length = bis.read(buffer)) > 0;) {
			        bos.write(buffer, 0, length);
			    }
				bos.flush();
				bos.close();
			}
			
			return;
			
		} catch(IOException | SQLException e) {
			e.printStackTrace();
			return;
		}
	}
	
	
}
