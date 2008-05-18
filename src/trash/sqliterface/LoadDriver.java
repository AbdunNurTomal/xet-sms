/*import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;*/
import java.sql.*;

// Notice, do not import com.mysql.jdbc.* // or you will have problems!

public class LoadDriver {
	public static void main(String[] args) {
		try {
			// The newInstance() call is a work around for some
			// broken Java implementations

            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
			System.out.println(ex);
			// handle the error
		}

        try {
		Connection conn = DriverManager.getConnection("jdbc:mysql://mainserver/irc_bot?user=root&password=texrulez");

		        // Do something with the Connection
		        Statement stmt = null;
				ResultSet rs = null;

				try {
				    stmt = conn.createStatement();
				    //rs = stmt.executeQuery("select * from citazioni where autore = 'Ciccio' order by rand() limit 0,1");

				    // or alternatively, if you don't know ahead of time that
				    // the query will be a SELECT...

				    if (stmt.execute("select citazione from citazioni where autore = 'Ciccio' order by rand() limit 0,1")) {
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
					// handle the error

				} finally {
				    // it is a good idea to release
				    // resources in a finally{} block
				    // in reverse-order of their creation
				    // if they are no-longer needed

				    if (rs != null) {
				        try {
				            rs.close();
				        } catch (SQLException sqlEx) {
							// ignore
							System.out.println(sqlEx);
						}

				        rs = null;
				    }

				    if (stmt != null) {
				        try {
				            stmt.close();
				        } catch (SQLException sqlEx) {
							System.out.println(sqlEx);
							// ignore
				        }

				        stmt = null;
				    }
				}

		    } catch (SQLException ex) {
				// handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
		}
	}

