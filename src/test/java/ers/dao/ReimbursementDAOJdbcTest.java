package ers.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;

import org.junit.BeforeClass;

import com.revature.util.ConnectionUtil;

public class ReimbursementDAOJdbcTest {

	static BigDecimal amount = new BigDecimal("130.15");
	static LocalDateTime submitted = LocalDateTime.of(1985, 12, 28, 2, 30, 0, 0);
	static LocalDateTime resolved = null;
	static String description = "This is a reimbursement request.";
	 
	
	@BeforeClass
	public static void setup() throws Exception {
		Connection conn = ConnectionUtil.getConnection();
		conn.setAutoCommit(false);
	}

}
