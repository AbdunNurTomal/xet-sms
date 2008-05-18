import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
//import java.sql.*;

/**	This would be called by cmd script from mIRC
*/
public class SQLmIRCInterface {

	/**	mantain connection with given db
	*/
	public Connection conn;
	/**	Mantain Statement
	*/
	public Statement stmt;
	/**	Mantain multiples results from db querys
	*/
	public ResultSet rs;

	public void SQLmIRCInterface() {
		//conn = null;
		//JdbcSqlServerLink = "jdbc:mysql://localhost/test?user=monty&password=greatsqldb";
		//randomQuoteFromAutorQueryLine = "select citazione from citazioni where autore = 'Ciccio' order by rand() limit 0,1";
		//addQuoteQueryLine = "";
		stmt = null;
		rs = null;
		registerJDBCDriver();
	}

	/**	register MySQL Connector with JDBC Driver Manager
	*/
	private static void registerJDBCDriver() {
		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }

        System.out.println("register driver: OK");

	}
	/**	opens a connection with MySQL Server
	*/
	private void openConnection(String JdbcSqlServerLink) {
		registerJDBCDriver();
		try {
            conn = DriverManager.getConnection(JdbcSqlServerLink);
          	// Do something with the Connection
			this.getQuote("Ciccio");
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
	}
	/**	Exec a query to db and returns
		a random quote from author
		@param author who you wanna quote
		@return random quote from author
	*/
	public void getQuote(String author) {
		// Do something with the Connection
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();
			if (stmt.execute("select citazione from citazioni where autore = '" + author + "' order by rand() limit 0,1")) {
				rs = stmt.getResultSet();
				System.out.println("SELECT ok");
			}
			else {
				System.out.println("cazzoooo qualcosa è andato storto!!!");
			}
			// importante altrimenti non va un cazzo
			// porta il puntatore al primo elemento di rs
			rs.first();

			// Now do something with the ResultSet ....
			System.out.println(rs.getString("citazione"));
		} catch (SQLException sqlEx) {
			System.out.println("SQL Exception: "+sqlEx.getSQLState() + "\n" + sqlEx);
		} catch (Exception ex) {
			System.out.println("Exception: "+ex);
		} finally {
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
		}
	}

	/**	Exec a query to db and adds
		a quote from given author
		@param autor who said quote
		@param quote what author said
	*/
	public void addQuote(String author, String quote) {

	}

	/**	Manage command line params
		and interface with irc
	*/
/*	public static void main(String[] args) {

		SQLmIRCInterface prova = new SQLmIRCInterface();
		prova.openConnection("jdbc:mysql://mainserver/irc_bot?user=root&password=texrulez");
		//prova.getQuote("Ciccio");


		return;
	}
*/


}