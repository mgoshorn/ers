package com.revature.services;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.revature.beans.User;
import com.revature.dao.UsersDAO;
import com.revature.dao.UsersDAOJdbc;

public class UserService {
	
	UsersDAO dao = new UsersDAOJdbc();
	private static Logger log = Logger.getRootLogger();
	private static Properties prop = new Properties();	
	private static final int PASSWORD_MIN_LENGTH;
	private static final boolean PASSWORD_REQUIRES_NUMBER;
	private static final boolean PASSWORD_REQUIRES_SPECIAL_CHAR;
	
	static {
		//Load properties files and 
		try {
			prop.load(UserService.class.getClassLoader().getResourceAsStream("ers.properties"));
			//prop.load(new FileReader("src/main/resources/ers.properties"));
		} catch (FileNotFoundException e) {
			log.warn("Failed to load ers.properties");
			e.printStackTrace();
		} catch (IOException e) {
			log.warn("Failed to load ers.properties");
			e.printStackTrace();
		}
		PASSWORD_MIN_LENGTH = Integer.valueOf(prop.getProperty("PasswordMinLength", "8"));
		PASSWORD_REQUIRES_NUMBER = Boolean.valueOf(prop.getProperty("PasswordReqNumber", "true"));
		PASSWORD_REQUIRES_SPECIAL_CHAR = Boolean.valueOf(prop.getProperty("PasswordReqSpecialCharacter", "true"));
	}
	
	/**
	 * Attempts to update a password given a User object and a password String
	 * @param user - User to have password updated
	 * @param newPassword - password to use
	 * @return true if successful, false otherwise
	 */
	public boolean updatePassword(User user, String newPassword) {
		newPassword = newPassword.trim();
		Map<String, String> hashSaltMap;
		
		String oldHash;
		
		try {
			hashSaltMap = dao.getStoredHashAndSalt(user);
			oldHash = hashSaltMap.get("hash");
		} catch (SQLException e) {
			log.error("Failed to retrieve old hash for password update.");
			e.printStackTrace();
			return false;
		}

		String salt = generateSalt();
		String newHash = hash(newPassword, salt);
		
		if(!validate(newPassword, newHash, oldHash)) return false;
		
		return dao.updatePasshash(user, newHash, salt);
	}
	
	/**
	 * Validates that a password meets all restrictions
	 * Helper method to {@link #updatePassword(User, String)}
	 * @param newPassword - New password String
	 * @param newHash - Hash of new password String
	 * @param oldHash - Previous password hash
	 * @return true if password passes validation, false otherwise
	 */
	private boolean validate(String newPassword, String newHash, String oldHash) {		
		if(!meetsLengthReq(newPassword)) {
			log.info("new password failed length requirement.");
			return false;
		}
		
		if(!meetsContainsNumberReq(newPassword)) {
			log.info("new password failed contains number requirement");
			return false;
		}
		
		if(!meetsContainsSpecialCharReq(newPassword)) {
			log.info("new password failed requires special char requirement");
			return false;
		}
		
		if(hashesMatch(newHash, oldHash)) {
			log.info("new password failed not same password req");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Generate a new salt String to use with a new account
	 * Generated String will have alphanumeric capitalized characters.
	 * @return salt string
	 */
	private String generateSalt() {
		Random rand = new Random(System.nanoTime());
		String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		
		String salt = "";
		for(int i = 0; i < 40; i++) {
			salt += str.charAt(rand.nextInt(str.length()));
		}
		
		return salt;
	}
	
	/**
	 * Validates that a password meets length requirement
	 * Helper method to {@link #validate(String, String, String)}
	 * @param password - password to validate
	 * @return true if password passes validation, false otherwise
	 */
	private boolean meetsLengthReq(String password) {
		return password.length() >= PASSWORD_MIN_LENGTH;
	}
	
	/**
	 * Validates that a password meets contains a number requirement
	 * Helper method to {@link #validate(String, String, String)}
	 * @param password - password to validate
	 * @return true if password passes validation, false otherwise
	 */
	private boolean meetsContainsNumberReq(String password) {
		//Return true if requirement is disabled
		if(!PASSWORD_REQUIRES_NUMBER) return true;
		
		Pattern pattern = Pattern.compile("[0-9]", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(password);
		return matcher.find();
	}
	
	/**
	 * Validates that a password meets contains a special char requirement
	 * Helper method to {@link #validate(String, String, String)}
	 * @param password - password to validate
	 * @return true if password passes validation, false otherwise
	 */
	private boolean meetsContainsSpecialCharReq(String password) {
		//Return true if requirement is disabled
		if(!PASSWORD_REQUIRES_SPECIAL_CHAR) return true;		
		
		Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(password);
		return matcher.find();
	}
	
	/**
	 * Compares to hashes and returns true when they match
	 * @param hashOne - First hash 
	 * @param hashTwo - Second hash
	 * @return return true if hashes match, false otherwise
	 */
	private boolean hashesMatch(String hashOne, String hashTwo) {
		return !hashOne.equals(hashTwo);
	}
	
	/**
	 * Authenticates a user given the user object and input password hash
	 * @param user - User object
	 * @param hash - Hash String
	 * @return true if authentic, false otherwise
	 */
//	private boolean authenticate(User user, String hash) {
//		String oldHash;
//		Map<String, String> hashSaltMap;
//		
//		//Get old hash from dao
//		try {
//			hashSaltMap = dao.getStoredHashAndSalt(user);
//			oldHash = hashSaltMap.get("hash");
//		} catch(SQLException e) {
//			log.error("Password validation failed due to SQL Exception. Can not authenticate user.");
//			log.error(e.toString());
//			return false;
//		}
//		
//		return hashesMatch(hash, oldHash);
//	}
	
	public boolean authenticate(String username, String password) {
		Map<String, String> hashSalt = dao.getStoredHashAndSalt(username);
		String inputHash = hash(password, hashSalt.get("salt"));
		return hashSalt.get("hash").equals(inputHash);
	}
	
	/**
	 * Hash password
	 * @param password - password String
	 * @return Password hash String
	 */
	private String hash(String password, String salt) {
		String fullHashable = password + salt;
		return DigestUtils.sha256Hex(fullHashable); 
	}
	
}
