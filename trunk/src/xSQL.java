import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;

import org.jsmsengine.*;

import java.util.Date;

// Notice, do not import com.mysql.jdbc.*
// or you will have problems!

public class xSQL {
  
  private Connection conn;
  private Statement stmt;
  private ResultSet rs;
  private String db;
  
  /**
    default constructor: creates an object associated
    with given db for ex: jdbc:mysql://localhost/xsms?user=root&password=changeme
    @param db a database url of the form  jdbc:subprotocol:subname
  */
  public xSQL(String db) {
    try {
      // The newInstance() call is a work around for some
      // broken Java implementations
      Class.forName("com.mysql.jdbc.Driver").newInstance();
    } 
    catch (Exception ex) {
      System.out.println("Exception "+ex);
      // handle the error
    }
    conn = null;
    stmt = null;
    rs = null;
    this.db = db;
  }
  
  /**
    Checks is the given cellNumber is in the db
    @param cellNumber string rapresenting the number you wanna check
    @returns true if the number is included in db, false if not
  */
  public boolean numberIsKnown(String cellNumber) {
    boolean result = false;
    try {
      conn = DriverManager.getConnection(db);
      stmt = conn.createStatement();
      rs = stmt.executeQuery("SELECT cellnumber FROM users WHERE cellnumber = '" + cellNumber + "' LIMIT 0,1");
      result = rs.first();
    } 
    catch (SQLException sqlEx) {
      while( sqlEx != null) {
        System.out.println("SQLException: " + sqlEx.getMessage());
        System.out.println("SQLState: " + sqlEx.getSQLState());
        System.out.println("VendorError: " + sqlEx.getErrorCode());
        sqlEx = sqlEx.getNextException();
      } 
    }
    finally { this.close(); }
    return result;
  }
  
  /**
    put given message into the db
    @param sms Message object rapresenting the sms.
    @returns number of affected rows: 1 if everything is fine
  */
  public int insertSMS(CMessage sms) {
    int result = -1;
    try {
      conn = DriverManager.getConnection(db);
      stmt = conn.createStatement();
      result = stmt.executeUpdate(
        "INSERT INTO messages (sender, date, time, text, status)"+ 
        "VALUES ('"+sms.getOriginator()+"', '"+
          (sms.getDate().getYear()+1900)+"-"+(sms.getDate().getMonth()+1)+"-"+sms.getDate().getDate()+"', '"+
          (sms.getDate()).getHours()+":"+(sms.getDate()).getMinutes()+":"+(sms.getDate()).getSeconds()+"', '"+
          sms.getText().replace('\'',' ')+"', '0')" //due to a bug with sms containning '.
      );
    } 
    catch (SQLException sqlEx) {
      // handle any errors
      while( sqlEx != null) {
        System.out.println("SQLException: " + sqlEx.getMessage());
        System.out.println("SQLState: " + sqlEx.getSQLState());
        System.out.println("VendorError: " + sqlEx.getErrorCode());
        sqlEx = sqlEx.getNextException();
      } 
    }
    finally { this.close(); }  
    return result;
  }


  /**
    put given message into the db of Sent messages
    @param sms Message object rapresenting the sms.
    @returns number of affected rows: 1 if everything is fine
  */
  public int insertSentSMS(CMessage sms) {
    int result = -1;
    try {
      conn = DriverManager.getConnection(db);
      stmt = conn.createStatement();
      result = stmt.executeUpdate(
        "INSERT INTO sentmessages (recipient, date, time, text, isFlash)"+ 
        "VALUES ('"+sms.getRecipient()+"', '"+
          (sms.getDate().getYear()+1900)+"-"+(sms.getDate().getMonth()+1)+"-"+sms.getDate().getDate()+"', '"+
          (sms.getDate()).getHours()+":"+(sms.getDate()).getMinutes()+":"+(sms.getDate()).getSeconds()+"', '"+
          sms.getText().replace('\'',' ')+"', '0')" //due to a bug with sms containning '.
      );
    } 
    catch (SQLException sqlEx) {
      // handle any errors
      while( sqlEx != null) {
        System.out.println("SQLException: " + sqlEx.getMessage());
        System.out.println("SQLState: " + sqlEx.getSQLState());
        System.out.println("VendorError: " + sqlEx.getErrorCode());
        sqlEx = sqlEx.getNextException();
      } 
    }
    finally { this.close(); }  
    return result;
  }

  /**
    put given user into the db
    @param usr User object rapresenting the user.
    @returns number of affected rows: 1 if everything is fine
  */  
  public int insertUser(User usr) {
    int result = -1;
    try {
      conn = DriverManager.getConnection(db);
      stmt = conn.createStatement();
      result = stmt.executeUpdate(
        "INSERT INTO users (firstname, secondname, nickname, cellnumber, miscinfo, groups) VALUES ('"+
          usr.getFirstname()+"', '"+
          usr.getSecondname()+"', '"+
          usr.getNickname()+"', '"+
          usr.getNumber()+"', '"+
          usr.getDescription()+"', '"+
          usr.getGroups()+
        "')"
      );
    } 
    catch (SQLException sqlEx) {
      // handle any errors
      while( sqlEx != null) {
        System.out.println("SQLException: " + sqlEx.getMessage());
        System.out.println("SQLState: " + sqlEx.getSQLState());
        System.out.println("VendorError: " + sqlEx.getErrorCode());
        sqlEx = sqlEx.getNextException();
      } 
    }
    finally { this.close(); }  
    return result;
  }
  /**
    check if there are any messages to send
    @returns true only if there is one or more
    messages waiting to be sent
  */
  public boolean checkOutgoingMessages(){    
    boolean result = false;
    try {
      conn = DriverManager.getConnection(db);
      stmt = conn.createStatement();
      rs = stmt.executeQuery("select * from outgoingMessages limit 0,10");
      result = rs.first();
    } 
    catch (SQLException sqlEx) {
      // handle any errors
      while( sqlEx != null) {
        System.out.println("SQLException: " + sqlEx.getMessage());
        System.out.println("SQLState: " + sqlEx.getSQLState());
        System.out.println("VendorError: " + sqlEx.getErrorCode());
        sqlEx = sqlEx.getNextException();
      } 
    }
    finally { this.close(); }  
    return result;
  }
  
  /**
    return the first message wainting to be sent
    @returns first outgoing message
  */
  public COutgoingMessageWithID getFirstOutgoingMessage(){    
    COutgoingMessageWithID result = null;
    try {
      conn = DriverManager.getConnection(db);
      stmt = conn.createStatement();
      rs = stmt.executeQuery("select * from outgoingMessages limit 0,1");
      if(rs.first()){
        result = new COutgoingMessageWithID(rs.getString("recipient"), rs.getString("text"), rs.getInt("id"));
        result.setFlashSms(rs.getBoolean("isFlash"));
      }
    } 
    catch (SQLException sqlEx) {
      // handle any errors
      while( sqlEx != null) {
        System.out.println("SQLException: " + sqlEx.getMessage());
        System.out.println("SQLState: " + sqlEx.getSQLState());
        System.out.println("VendorError: " + sqlEx.getErrorCode());
        sqlEx = sqlEx.getNextException();
      } 
    }
    finally { this.close(); }  
    return result;
  }
  
  /**
    return a ResultSet of message wainting to be sent
    @returns ResultSet of outgoing message
  */
  public ResultSet getAllOutgoingMessage(){ 
    try {
      conn = DriverManager.getConnection(db);
      stmt = conn.createStatement();
      rs = stmt.executeQuery("select * from outgoingMessages");
    } 
    catch (SQLException sqlEx) {
      // handle any errors
      while( sqlEx != null) {
        System.out.println("SQLException: " + sqlEx.getMessage());
        System.out.println("SQLState: " + sqlEx.getSQLState());
        System.out.println("VendorError: " + sqlEx.getErrorCode());
        sqlEx = sqlEx.getNextException();
      } 
    }
    finally { this.close(); }  
    return rs;
  }
  
  /**
    removes outgoing message corresponding to the given id
    @param id ID of the message to delete
    @returns true if message was deleted 
  */
  public boolean deleteOutgoingMessage(int id){
    boolean result = false;
    try {
      conn = DriverManager.getConnection(db);
      stmt = conn.createStatement();
      if(stmt.execute("delete from outgoingMessages where (id='"+id+"')")){
        rs = stmt.getResultSet();
        result = (stmt.getUpdateCount()==1);
      }
    } 
    catch (SQLException sqlEx) {
      // handle any errors
      while( sqlEx != null) {
        System.out.println("SQLException: " + sqlEx.getMessage());
        System.out.println("SQLState: " + sqlEx.getSQLState());
        System.out.println("VendorError: " + sqlEx.getErrorCode());
        sqlEx = sqlEx.getNextException();
      } 
    }
    finally { this.close(); }  
    return result;
    
  }
  
  /** 
    releases resources in reverse-order 
    of their creation when they are no-longer needed 
  */  
  private void close(){
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException sqlEx) { // ignore }
      rs = null;
      }
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException sqlEx) { // ignore }
          stmt = null;
        }
      }
    }
  }


  public static void main(String args[]){
    /*if((new xSQL("jdbc:mysql://localhost/xsms?user=root&password=texrulez")).numberIsKnown("3493526168"))
      System.out.println("si");
    else System.out.println("no");*/
    Date date = new Date();
    CMessage msg = new CMessage(CMessage.TYPE_INCOMING, 
      date,
      "+393493526168",
      "",
      "dio stramaledetto",
      0
    );
    xSQL sql = new xSQL("jdbc:mysql://localhost/xsms?user=root&password=texrulez");
    User usr = new User("Rosario","Perez","djRos","+393388808518","xtremely cool","users");
    if(!sql.numberIsKnown(usr.getNumber()))System.out.println(sql.insertUser(usr));
    //sql.insertSMS(msg);
    //sql.deleteOutgoingMessage(0);
    //System.out.println(sql.checkOutgoingMessages());
    //System.out.println(date.getHours()+":"+date.getMinutes()+":"+date.getSeconds());
  }
}

/**
  associates a super COutgoingMessage with the relative db ID.
*/
class COutgoingMessageWithID extends COutgoingMessage {
  private int dbid;
  
  COutgoingMessageWithID(int dbid){
    super();
    this.dbid = dbid;
  }
  
  COutgoingMessageWithID(String recipient, String text, int dbid){
    super(recipient, text);
    this.dbid = dbid;
  }
  
  public int getDBId(){return dbid;}
}