package ers.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.revature.services.UserService;

public class UserServiceTests {
	Logger log = Logger.getRootLogger();
	private UserService service = new UserService();
	
	/**
	 * Test salt generation such that it fits constraints
	 * ha sufficient distribution
	 * @throws Exception
	 */
	@Test
	public void generateSaltTest() throws Exception {
		
		//Make method accessible
		Method genSalt = UserService.class.getDeclaredMethod("generateSalt");
		genSalt.setAccessible(true);
		
		String salt = (String)genSalt.invoke(service);
		
		log.info(salt);
		
		//Salt should be 40 characters
		assertTrue(salt.length() == 40);
		
		//Salt should contain only alphanumeric characters
		assertFalse(salt.contains("^[a-zA-Z0-9]*$"));
		
		Set<String> stringSet = new HashSet<>();
		
		//tolerance of collisions
		int tolerance = 1;
		int collisions = 0;
		
		for(int i = 0; i < 100000; i++) {
			String newSalt = (String)genSalt.invoke(service);
			String internedSalt = newSalt.intern();
			if(stringSet.contains(internedSalt)) collisions++;
			stringSet.add(internedSalt);
			
			//If number of collisions is greater than tolerance, throw exception
			assertTrue(collisions <= tolerance);
		}
	}
	
	@Test
	public void specialCharactersReqTest() throws Exception {
		String allAlpha = "abcdefghijklmonop";
		String allNum = "234567876543";
		String mixAlphaNum = "lkjafsld12884";
		String spaces = "                    ";
		String mixSpecial = "jlls&31!**32";
		String testTest = "testingacc1!";
		
		Method specialChars = UserService.class.getDeclaredMethod("meetsContainsSpecialCharReq", String.class);
		specialChars.setAccessible(true);
		
		
		assertFalse((boolean)specialChars.invoke(service, allAlpha));
		assertFalse((boolean)specialChars.invoke(service, allNum));
		assertFalse((boolean)specialChars.invoke(service, mixAlphaNum));
		assertTrue ((boolean)specialChars.invoke(service, spaces));
		assertTrue ((boolean)specialChars.invoke(service, mixSpecial));
		assertTrue ((boolean)specialChars.invoke(service, testTest));
	}
}
