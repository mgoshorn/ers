package ers.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.beans.ReimbursementStatus;
import com.revature.beans.Role;
import com.revature.util.ConnectionUtil;

/**
 * Test checks values in ReimbursementStatus database table and assures us that the values stored in the database
 * match the values defined in their correlating Enum instance and that the Enum instance methods resolve to appropriate results.
 *
 * @author Mitch Goshorn
 */
public class UserRoleUnitTests {
	private static Logger log = Logger.getRootLogger();
	private static Map<Integer, String> valueMap = new HashMap<Integer, String>();
	
	/**
	 * Setup to map database id values to Strings for later checks
	 * @throws SQLException
	 */
	@BeforeClass
	public static void setup() throws SQLException {
		String query = "SELECT * FROM user_roles";
		
		Connection conn = ConnectionUtil.getConnection();
		PreparedStatement preparedStatement = conn.prepareStatement(query);
		ResultSet results = preparedStatement.executeQuery();
		
		while(results.next()) {
			Integer key = results.getInt("USER_ROLE_ID");
			String value = results.getString("USER_ROLE");
			valueMap.put(key, value);
		}
	}
	
	/**
	 * Test checks whether values in the Enum match values in the database
	 * @throws SQLException
	 */
	@Test
	public void checkEnumValuesMatchDatabase() throws SQLException {
		for(Role role : Role.values()) {
			assertEquals(role.toString(), valueMap.get(role.getValue()));
		}
		
		log.trace("valueMap size -> "+valueMap.size()+", Role values length -> " + ReimbursementStatus.values().length);
		assertTrue(valueMap.size() == Role.values().length);
	}
	
}
