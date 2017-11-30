package com.revature.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.revature.beans.Role;
import com.revature.beans.User;
import com.revature.util.ConnectionUtil;

public class UsersDAOJdbc implements UsersDAO {

	private static Logger log = Logger.getRootLogger();
	
	/**
	 * Instantiates a user object from a result set
	 * @param results - result set
	 * @return - User object
	 * @throws SQLException
	 */
	private User getUserFromResultSet(ResultSet results) throws SQLException {
		Integer id = results.getInt("USER_ID");
		String username = results.getString("USERNAME");
		String firstName = results.getString("FIRSTNAME");
		String lastName = results.getString("LASTNAME");
		String email = results.getString("USER_EMAIL");
		Role role = Role.getByValue(results.getInt("USER_ROLE_ID"));
		
		return new User(id, username, firstName, lastName ,email, role);
	}
	
	@Override
	public User getUserByUsername(String username) {
		try(Connection conn = ConnectionUtil.getConnection()) {
			return getUserByUsername(username, conn);
		} catch(SQLException e) {
			log.warn("Error attempting to get user from database.");
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public User getUserByUsername(String username, Connection conn) throws SQLException {
		String query = "SELECT * FROM users WHERE username = ?";
		log.trace("Searching for user with username: " + username);
		
		PreparedStatement preparedStatement = conn.prepareStatement(query);
		
		preparedStatement.setString(1, username);
		
		ResultSet results = preparedStatement.executeQuery();
		
		User user = null;
		if(results.next()) {
			user = getUserFromResultSet(results);
			log.trace("User found with id: " + user.getId());
		}
		
		return user;	
	}

	@Override
	public User getUserByID(int id) throws SQLException {		
		Connection conn = ConnectionUtil.getConnection();
		return getUserByID(id, conn);
	}
	
	@Override
	public User getUserByID(int id, Connection conn) throws SQLException {
		String query = "SELECT user_id, username, firstname, lastname, user_email, user_role_id FROM users "
				+ "WHERE user_id = ?";
		
		
		PreparedStatement preparedStatement = conn.prepareStatement(query);
		
		preparedStatement.setInt(1, id);
		
		ResultSet results = preparedStatement.executeQuery();
		
		User user = null;
		if(results.next()) {
			user = getUserFromResultSet(results);
		}
		
		return user;
	}

	@Override
	public Map<String, String> getStoredHashAndSalt(User user) throws SQLException {
		try(Connection conn = ConnectionUtil.getConnection()) {
			return getStoredHashAndSalt(user.getUsername(), conn);
		} catch(SQLException e) {
			log.info("User failed validation.");
			return null;
		}
	}
	
	@Override
	public Map<String, String> getStoredHashAndSalt(String username) {
		try(Connection conn = ConnectionUtil.getConnection()) {
			return getStoredHashAndSalt(username, conn);
		} catch(SQLException e) {
			log.info("User failed validation.");
			return null;
		}
	}
	
	
	private Map<String, String> getStoredHashAndSalt(String username, Connection conn) throws SQLException {
		Map<String, String> map = new HashMap<>();
		String query = "SELECT passhash, salt FROM users WHERE username = ?";
		
		PreparedStatement preparedStatement = conn.prepareStatement(query);
		preparedStatement.setString(1, username);
		ResultSet results = preparedStatement.executeQuery();
		
		if(results.next()) {
			String hash = results.getString("PASSHASH");
			String salt = results.getString("SALT");
			
			map.put("hash", hash);
			map.put("salt", salt);
			
			return map;
		}
		
		//Record not found in database so return null
		return null;
	}
		
	
	@Override
	public boolean saveUser(User user) {		
		Connection conn = ConnectionUtil.getConnection();
		return saveUser(user, conn);
	}
	
	private boolean saveUser(User user, Connection conn) {
		String query = "UPDATE users SET "
				+ "username = ?, "
				+ "firstname = ?, "
				+ "lastname = ?, "
				+ "user_email = ?, "
				+ "user_role_id = ? "
				+ "WHERE user_id = ?";
		
		try {
			
			log.trace("Saving user");
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			
			preparedStatement.setString(1, user.getUsername());
			preparedStatement.setString(2, user.getFirstName());
			preparedStatement.setString(3, user.getLastName());
			preparedStatement.setString(4, user.getEmail());
			preparedStatement.setInt(5, user.getRole().getValue());
			preparedStatement.setInt(6, user.getId());
			preparedStatement.execute();
			return true;
		} catch(SQLException e) {
			log.error("User update failed due to an SQLException");
			log.error(e.toString());
			return false;
		}
	}
	

	@Override
	public Integer create(User user, String hash, String salt) {
		try(Connection conn = ConnectionUtil.getConnection()) {
			return create(user, hash, salt, conn);
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	

	public Integer create(User user, String hash, String salt, Connection conn) throws SQLException {
		//If user has already been saved, save it and return existent id
				if(user.getId() != null) {
					saveUser(user);
					return user.getId();
				}
				
				String query = "INSERT INTO users "
						+ "(username, passhash, firstname, lastname, user_email, user_role_id, salt) "
						+ "VALUES "
						+ "(?, ?, ?, ?, ?, ?, ?)";
				
		PreparedStatement preparedStatement = conn.prepareStatement(query, new String[] {"user_id"});
		preparedStatement.setString(1, user.getUsername());
		preparedStatement.setString(2, hash);
		preparedStatement.setString(3, user.getFirstName());
		preparedStatement.setString(4, user.getLastName());
		preparedStatement.setString(5, user.getEmail());
		preparedStatement.setInt(6, user.getRole().getValue());
		preparedStatement.setString(7, salt);
		preparedStatement.execute();
		
		ResultSet keys = preparedStatement.getGeneratedKeys();

		if(keys.next()) {
			int id = keys.getInt(1);
			user.setId(id);
			return id;
		}
		
		return null;
	}

	@Override
	public boolean updatePasshash(User user, String passhash, String salt) {
		Connection conn = ConnectionUtil.getConnection();
		return updatePasshash(user, passhash, salt, conn);
	}
	
	private boolean updatePasshash(User user, String passhash, String salt, Connection conn) {
		String query = "UPDATE users SET passhash = ?, salt = ? WHERE user_id = ?";
		
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			
			preparedStatement.setString(1, passhash);
			preparedStatement.setString(2, salt);
			preparedStatement.setInt(3, user.getId());
			
			return preparedStatement.execute();
	
		} catch(SQLException e) {
			log.error("Update passhash/salt failed due to an SQLException");
			log.error(e.toString());
			return false;
		}
	}
	
	

}
