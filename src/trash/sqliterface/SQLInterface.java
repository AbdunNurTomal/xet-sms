import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


public class SQLInterface {

	/**	mantain connection with given db
	*/
	public Connection conn;
	/**	Mantain Statement
	*/
	public Statement stmt;
	/**	Mantain multiples results from db querys
	*/
	public ResultSet rs;

	private static void registerJDBCDriver() {
		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }
	}
	public void execQuery(String query) {
		//Statement stmt = null;
		//ResultSet rs = null;

		try {
			stmt = conn.createStatement();
			if (stmt.execute(query)) {
				rs = stmt.getResultSet();
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
	public void queryFuckingDB(String JdbcSqlServerLink, String query) {
		registerJDBCDriver();
		try {
            conn = DriverManager.getConnection(JdbcSqlServerLink);
			this.execQuery(query);
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
	}




	public static void main(String[] args) {

		SQLInterface prova = new SQLInterface();
		prova.queryFuckingDB(args[0], args[1]);
			/*"jdbc:mysql://mainserver/irc_bot?user=root&password=texrulez",
			"select citazione from citazioni where autore = "
			+"'Ciccio' order by rand() limit 0,1");*/


		return;
	}
}