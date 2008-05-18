import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**	manage sql connection */
public class SQLSMSInterface {

	/**	mantain connection with given db */
	public Connection conn;
    
	/**	Mantain Statement */
	//public Statement stmt;
    
	/**	Mantain multiples results from db querys */
	//public ResultSet rs;
  
  public SQLSMSInterface(String sql) {
    //stmt = null;
		//rs = null;
		//registerJDBCDriver();
    this.openConnection(sql);
  }

	/**	
    register MySQL Connector with JDBC Driver Manager
	*/
	private static void registerJDBCDriver() {
		try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
    }
    catch (Exception ex) {
      // handle the error
      System.out.println("register driver: FAILED\n"+ex);
    }
	}
  
	/**	
    opens a connection with MySQL Server
	*/
	private void openConnection(String JdbcSqlServerLink) {
		registerJDBCDriver();
		try {
      conn = DriverManager.getConnection(JdbcSqlServerLink);
    }
    catch (SQLException ex) {
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
	}
  
    /**
    checks if the sender's number is just in the db
    returns the id or true\false?
  */
  /*private boolean numberIsKnown(String cellNumber){
    return (execQuery("select cellnumber from users where cellnumber = '" + cellNumber + "' limit 0,1").first()) != null;
  }*/
  
	/**	Exec a query to db and adds given sms	*/
	public void addSMS(Message sms) {

	}
  
  /**
    exec given query tu current open db
    @param query mysql query
    @returns ResultSet with query's results
  */
  private ResultSet execQuery(String query) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			if (stmt.execute(query)) {
        if(stmt.getResultSet()!=null)
          rs = stmt.getResultSet();
        else return null;
				System.out.println("query: OK");
			}
			else {
        System.out.println("query: FAILED");
      }
			// importante altrimenti non va un cazzo
			// porta il puntatore al primo elemento di rs
			rs.first();
      if(rs == null)System.out.println("non ce n'e'");
      //System.out.println("numero: "+rs.getString("cellnumber"));
		} 
    catch (SQLException sqlEx) {    
      while( sqlEx != null) {
      System.out.println("SQL Exception: "+sqlEx.getErrorCode());// + "\n" + sqlEx);
      if(sqlEx.getErrorCode() == 0)System.out.println("nun ce sta na sega");
      sqlEx = sqlEx.getNextException();
      } 
		} 
    catch (Exception ex) { 
      System.out.println("Exception: "+ex);
		}
    return rs;
  }
  
  /** bah...inutile...
  private void closeConnection() {
		// it is a good idea to release
		// resources in a finally{} block
		// in reverse-order of their creation
		// if they are no-longer needed
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx);
			}
			rs = null;
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx);
			}
			stmt = null;
		}
  }*/
  
  public static void main(String args[]) {
    SQLSMSInterface prova = new SQLSMSInterface("jdbc:mysql://localhost/xsms?user=root&password=texrulez");
    ResultSet asd = prova.execQuery("select * from users where cellnumber = 3493526169 limit 0,1");
    //if (prova.numberIsKnown("vaccamadonna"))System.out.println("si");
    //else System.out.println("no");
    
    
    try { 
      asd.first();
      if (asd == null) System.out.println("query vuota");
      System.out.println(asd.getString("cellnumber")); 
    }
    catch (SQLException sqlEx) {
			System.out.println("SQL Exception2: "+sqlEx.getSQLState());// + "\n" + sqlEx);
    }
    catch (Exception ex) {
			System.out.println("Exception2: "+ex);
    }/**/
  }

}