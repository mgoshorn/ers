package com.revature.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.revature.beans.User;

public interface UsersDAO {

	/**
	 * Retrieves a User object from database by Username
	 * 
	 * @param username - provided username
	 * @return User object with provided username, or null if not found
	 * @throws SQLException
	 */
	public User getUserByUsername(String username);
	
	/**
	 * Retrieves a User object from database by Username
	 * @param username - provided username
	 * @param conn - Connection instance
	 * @return User object with provided username, or null if not found
	 * @throws SQLException
	 */
	public User getUserByUsername(String username, Connection conn) throws SQLException;
	
	/**
	 * Retrieves a User object from database by ID
	 * @param id - provided ID
	 * @return User object with provided ID, or null if not found
	 * @throws SQLException
	 */
	public User getUserByID(int id) throws SQLException;
	
	/**
	 * Retrieves and returns a stored hash from database for the 
	 * specified user instance.
	 * @param user - user whose password hash is to be retrieved
	 * @return password hash as String
	 * @throws SQLException
	 */
	public Map<String,String> getStoredHashAndSalt(User user) throws SQLException;
	
	public Map<String,String> getStoredHashAndSalt(String username);
	
	/**
	 * Saves a user instance to database
	 * @param user - instance to be stored
	 * @return true if successful, false otherwise
	 * @throws SQLException
	 */
	public boolean saveUser(User user) throws SQLException;
	
	/**
	 * Updates a password hash for a specified user instance
	 * @param user - Instance to have hash updated
	 * @param passhash - new hash
	 * @return true if successful, false otherwise
	 */
	public boolean updatePasshash(User user, String passhash, String salt);
	
	/**
	 * Creates a new user account with a supplied user object and password hash
	 * This method should be used when creating a new user, but if called on a user with
	 * an existing user id, it will save the user rather than create a new user.
	 * Gets a connection and then delegates to {@link #create(User, String, String, Connection)}
	 * 
	 * @param user - user instance to create in the database
	 * @param hash - users pass/salt hash
	 * @param salt - users generated salt
	 * @return generated id
	 * @throws SQLException
	 */
	public Integer create(User user, String hash, String salt);
	
	/**
	 * Creates a new user in the database given a user object, hash, and salt
	 * 
	 * @param user - user instance to create in the database
	 * @param hash - users pass/salt hash
	 * @param salt - users generated salt
	 * @param conn - database connection
	 * @return generated id as Integer or null when insert fails
	 * @throws SQLException 
	 */
	public Integer create(User user, String hash, String salt, Connection conn) throws SQLException;
	
}
