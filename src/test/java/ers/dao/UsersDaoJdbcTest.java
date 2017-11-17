package ers.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.revature.beans.Role;
import com.revature.beans.User;
import com.revature.dao.UsersDAO;
import com.revature.dao.UsersDAOJdbc;
import com.revature.services.UserService;
import com.revature.util.ConnectionUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsersDaoJdbcTest {
	static Logger log = Logger.getRootLogger();
	static UsersDAO dao = new UsersDAOJdbc();
	static Connection conn = ConnectionUtil.getConnection();
	static User user;
	static String password = "TestingPassword";
	static String salt = "1234567890123456789012345678901234567890";
	static Integer id = null;
	static String hash;
	
	/**
	 * Set auto-commit to false so inserted data will not persist after the test
	 * @throws Exception
	 */
	@BeforeClass
	public static void setup() throws Exception {
		conn.setAutoCommit(false);
		//conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		user = new User();		
		
		user.setUsername("TestingUserUsername");
		user.setFirstName("Testy");
		user.setLastName("McTestFace");
		user.setEmail("testy@mctest.face");
		user.setRole(Role.EMPLOYEE);
	}
	
	/**
	 * Test creation of a new User database entry
	 * @throws Exception
	 */
	@Test
	public void createTest() throws Exception {
		
		//Allow access to hashing method
		UserService service = new UserService();
		Method hashingMethod = UserService.class.getDeclaredMethod("hash", String.class, String.class);
		hashingMethod.setAccessible(true);
		
		//get hash
		hash = (String)hashingMethod.invoke(service, password, salt);
		
		id = dao.create(user, hash, salt, conn);
	}
	
	/**
	 * Tests to make sure retrieved user data matches original data
	 * @throws Exception
	 */
	@Test
	public void getUserByUsernameTest() throws Exception {
		User retrievedUser = dao.getUserByUsername(user.getUsername(), conn);
		
		//Should the DB retrieved object even be recognized by Java as the same object?
		//Why does this work at all?		
		assertEquals(user, retrievedUser);
	}
	
	@Test
	public void getUserByIDTest() throws Exception {
		Method getUserByIDMethod = dao.getClass().getDeclaredMethod("getUserByID", Integer.class, Connection.class);
		getUserByIDMethod.setAccessible(true);
		
		
		User retrievedUser = (User)getUserByIDMethod.invoke(dao, id, conn);
		assertEquals(user, retrievedUser);
	}
	
	@Test
	public void getStoredHashAndSaltTest() throws Exception {
		Method getStoredHashAndSaltMethod = dao.getClass().getDeclaredMethod("getStoredHashAndSalt", User.class, Connection.class);
		getStoredHashAndSaltMethod.setAccessible(true);
		
		Map<String, String> map = (Map<String, String>) getStoredHashAndSaltMethod.invoke(dao, user, conn);
		
		String retrievedHash = map.get("hash");
		String retrievedSalt = map.get("salt");
		
		assertEquals(retrievedHash, hash);
		assertEquals(retrievedSalt, salt);
	}
	
	/**
	 * Test methods for saving user, then tests that data retrieved match the saved data
	 * @throws Exception
	 */
	@Test
	public void saveUserTest() throws Exception {
		//Alter user object
		String newUsername = "ChangedUsername";
		user.setUsername(newUsername);
		
		//Get access to save method
		Method saveUser = dao.getClass().getDeclaredMethod("saveUser", User.class, Connection.class);
		saveUser.setAccessible(true);
		
		//Invoke save method 
		assertTrue((Boolean)saveUser.invoke(dao, user, conn));
		
		//Get access to getUserByID method
		Method getUserByIDMethod = dao.getClass().getDeclaredMethod("getUserByID", Integer.class, Connection.class);
		getUserByIDMethod.setAccessible(true);
				
		//Invoke method
		User retrievedUser = (User)getUserByIDMethod.invoke(dao, user.getId(), conn);

		//Compare values
		log.trace("Old user object username: " + user.getUsername() + ", new user object username: " + retrievedUser.getUsername());
		assertEquals(retrievedUser.getUsername(), newUsername);	
	}
	
	/**
	 * Updates password and hash info in test account, then asserts retrieved data matches
	 * updated data
	 * @throws Exception
	 */
	@Test
	public void updatePassHash() throws Exception {
		//Values updating to
		String newPass = "AlteredPass";
		String newSalt = "0987654321098765432109876543210987654321";
		
		//Getting access to hashing method from UserService class
		UserService service = new UserService();
		Method hashingMethod = UserService.class.getDeclaredMethod("hash", String.class, String.class);
		hashingMethod.setAccessible(true);
		
		//Hash password and salt to get new hash
		String newHash = (String) hashingMethod.invoke(service, newPass, newSalt);
		
		//Get access to updatePassHash method
		Method updatePasshash = dao.getClass().getDeclaredMethod("updatePasshash", User.class, String.class, String.class, Connection.class);
		updatePasshash.setAccessible(true);
		
		//invoke method to update data
		updatePasshash.invoke(dao, user, newHash, newSalt, conn);
		
		//Get access to method for retrieving pass/hash from database
		Method getStoredHashAndSaltMethod = dao.getClass().getDeclaredMethod("getStoredHashAndSalt", User.class, Connection.class);
		getStoredHashAndSaltMethod.setAccessible(true);
		
		//Get data
		Map<String, String> map = (Map<String, String>) getStoredHashAndSaltMethod.invoke(dao, user, conn);
		
		//Get returned data
		String retrievedHash = map.get("hash");
		String retrievedSalt = map.get("salt");
		
		//assert retrieved values match the values being updated to
		assertEquals(retrievedHash, newHash);
		assertEquals(retrievedSalt, newSalt);
	}
	
	/**
	 * Rollback all changes, to explicitly return database to its original state
	 * @throws Exception
	 */
	@AfterClass
	public static void breakdown() throws Exception {
		conn.rollback();
		conn.close();
	}
	
}
