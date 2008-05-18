
import java.sql.Connection;  
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

class xConfig{
  
  private String jdbcurl;
  private String comport;
  private int baudrate;
  private String gsmDeviceManufacturer;
  private String gsmDeviceModel;
  private String simpin;
  //private int demonWait;
  
  private Statement stmt;
  private ResultSet rs;
  private String id;
  
  public static final int REBOOT = 6;
  public static final int SHUTDOWN = 0;
  public static final int ISRUNNING = 3;
  public static final int CAN_SEND_SMS = 10;
  
  public xConfig(String jdbcurl, int id){
    this.jdbcurl = jdbcurl; 
    this.id = String.valueOf(id);
    this.getConfig();
  }  
  public xConfig(String jdbcurl, String id){
    this.jdbcurl = jdbcurl; 
    this.id = id;
    this.getConfig();
  }
  
  public boolean refresh(){return this.getConfig();}
  
  public void changeJDBCUrl(String jdbcurl, int id){
    this.jdbcurl = jdbcurl;
    this.id = String.valueOf(id);
    this.refresh();
  }
  
  private boolean getConfig(){       
    stmt = null;
    rs = null;
    boolean result = false;    
    try {
      // The newInstance() call is a work around for some
      // broken Java implementations
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      Connection conn = DriverManager.getConnection(this.jdbcurl);
      stmt = conn.createStatement();
      rs = stmt.executeQuery("Select * From config Where id = '"+id+"'");
      result = rs.first();
      this.comport = rs.getString("comport");
      this.baudrate = rs.getInt("baudrate");
      this.gsmDeviceManufacturer = rs.getString("gsmDeviceManufacturer");
      this.gsmDeviceModel = rs.getString("gsmDeviceModel");
      this.simpin = rs.getString("simpin");
      //this.demonWait = rs.getInt("demonWait");
      /*
      System.out.println(this.comport+"\n");
      System.out.println(this.baudrate+"\n");
      System.out.println(this.gsmDeviceManufacturer+"\n");
      System.out.println(this.gsmDeviceModel+"\n");
      System.out.println(this.simpin+"\n");
      */
      
    }
    catch (SQLException ex) {
      // handle any errors
      System.out.println("Errori durante il caricamento della configurazione");
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    } 
    catch (Exception ex) {
      // handle the error
      ex.printStackTrace();
    }  
    finally {
      this.relaseResources();
    }
    return result;
  }
  
  private void relaseResources(){
    // it is a good idea to release
    // resources in a finally{} block
    // in reverse-order of their creation
    // if they are no-longer needed
    if (rs != null) {
      try { rs.close(); } 
      catch (SQLException sqlEx) { rs = null; }
      if (stmt != null) {
        try { stmt.close(); } 
        catch (SQLException sqlEx) { stmt = null; }
      }
    }
  }
  
  public boolean test(int index){      
    stmt = null;
    rs = null;
    boolean result = false;
    
    String query = "";
    String row = "";
    switch(index){
      case xConfig.SHUTDOWN:
        query = "Select shutdown From config Where id = '"+id+"'";
        row = "shutdown";
      break;
      
      case xConfig.ISRUNNING:
        query = "Select isRunning From config Where id = '"+id+"'";
        row = "isRunning";
      break;
      
      case xConfig.REBOOT:
        query = "Select reboot From config Where id = '"+id+"'";
        row = "reboot";
      break;
      
      case xConfig.CAN_SEND_SMS:
        query = "Select sendMessagesAllowed From config Where id = '"+id+"'";
        row = "sendMessagesAllowed";
      break;
      
      default:
        query = "Select isRunning From config Where id = '"+id+"'";
        row = "isRunning";
      break;
    }
    try {
      // The newInstance() call is a work around for some
      // broken Java implementations
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      Connection conn = DriverManager.getConnection(this.jdbcurl);
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      if(rs.first())
        result = rs.getBoolean(row);      
    }
    catch (SQLException ex) {
      // handle any errors
      System.out.println("Errori durante il caricamento della configurazione");
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
    catch (Exception ex) {
      // handle the error
      ex.printStackTrace();
    }
    finally {
      this.relaseResources();
    }
    return result;
  }
  
  public int set(int index, int value){     
    stmt = null;
    rs = null;
    int result = -1;
    
    String query = "";
    switch(index){
      case xConfig.SHUTDOWN:
        query = "UPDATE config  SET shutdown='"+value+"' Where id = '"+id+"'";
      break;
      
      case xConfig.ISRUNNING:
        query = "UPDATE config  SET isRunning='"+value+"' Where id = '"+id+"'";
      break;
      
      case xConfig.REBOOT:
        query = "UPDATE config  SET reboot='"+value+"' Where id = '"+id+"'";
      break;
      
      case xConfig.CAN_SEND_SMS:
        query = "UPDATE config  SET SendMessagesAllowed='"+value+"' Where id = '"+id+"'";
      break;
      
      default:
        query = "UPDATE config  SET SendMessagesAllowed='"+value+"' Where id = '"+id+"'";
      break;
    }
    try {
      // The newInstance() call is a work around for some
      // broken Java implementations
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      Connection conn = DriverManager.getConnection(this.jdbcurl);
      stmt = conn.createStatement();
      result = stmt.executeUpdate(query);   
    }
    catch (SQLException ex) {
      // handle any errors
      System.out.println("Errori durante il caricamento della configurazione");
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
    catch (Exception ex) {
      // handle the error
      ex.printStackTrace();
    }
    finally {
      this.relaseResources();
    }
    return result;
  }
  
  public String getJdbcurl(){return jdbcurl;}
  public String getComport(){return comport;}
  public int getBaudrate(){return baudrate;}
  public String getGsmDeviceManufacturer(){return gsmDeviceManufacturer;}
  public String getGsmDeviceModel(){return gsmDeviceModel;}
  public String getSimpin(){return simpin;}
  
  public static void main(String args[]){
    xConfig x = new xConfig("jdbc:mysql://localhost/xsms?user=root&password=texrulez",1);
    System.out.println("Reboot: "+x.test(xConfig.REBOOT));
    System.out.println("Shutdown: "+x.test(xConfig.SHUTDOWN));
    System.out.println("IsRunning: "+x.test(xConfig.ISRUNNING));
    System.out.println("CanSendSMS: "+x.test(xConfig.CAN_SEND_SMS));
    
    System.out.println("\nSetting all to false");
    
    System.out.println("Reboot: "+x.set(xConfig.REBOOT,0));
    System.out.println("Shutdown: "+x.set(xConfig.SHUTDOWN,0));
    System.out.println("IsRunning: "+x.set(xConfig.ISRUNNING,0));
    System.out.println("CanSendSMS: "+x.set(xConfig.CAN_SEND_SMS,0));
    
    System.out.println("\nChecking Updates");
    
    System.out.println("Reboot: "+x.test(xConfig.REBOOT));
    System.out.println("Shutdown: "+x.test(xConfig.SHUTDOWN));
    System.out.println("IsRunning: "+x.test(xConfig.ISRUNNING));
    System.out.println("CanSendSMS: "+x.test(xConfig.CAN_SEND_SMS));
    
    System.out.println("\nSetting all to true");
    
    System.out.println("Reboot: "+x.set(xConfig.REBOOT,1));
    System.out.println("Shutdown: "+x.set(xConfig.SHUTDOWN,1));
    System.out.println("IsRunning: "+x.set(xConfig.ISRUNNING,1));
    System.out.println("CanSendSMS: "+x.set(xConfig.CAN_SEND_SMS,1));
    
    System.out.println("\nChecking Updates");
    
    System.out.println("Reboot: "+x.test(xConfig.REBOOT));
    System.out.println("Shutdown: "+x.test(xConfig.SHUTDOWN));
    System.out.println("IsRunning: "+x.test(xConfig.ISRUNNING));
    System.out.println("CanSendSMS: "+x.test(xConfig.CAN_SEND_SMS));
  }
}