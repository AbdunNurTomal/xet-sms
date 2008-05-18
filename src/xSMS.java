
import org.jsmsengine.*;

import java.util.*;
/*
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
*/
/**
  main class: 
  - handle serial connection with gsm device
  - handle mySQL connection with database
  - get sms, set in db
  - delete sms if inserction is ok
  - manage info about user and groups
*/
class xSMS
{
  private CService srv;
  private xSQL sql;

  public xSMS(CService srv, xSQL sql) {
    this.srv = srv;
    this.sql = sql;
  }
  
  /**
    gets sms and set into mysql
  */
  public boolean checkSMS() {
    LinkedList msgList = new LinkedList();
    boolean status = false;
		try {
      srv.connect();
			srv.readMessages(msgList, CIncomingMessage.CLASS_ALL);
      for (int i = 0; i < msgList.size(); i ++) {
        CIncomingMessage msg = (CIncomingMessage) msgList.get(i);
        System.out.println(msg);
        if(!sql.numberIsKnown(msg.getOriginator())){
          sql.insertUser(new User(msg.getOriginator()));
        }
        if(sql.insertSMS(msg)==1) {
          srv.deleteMessage(msg);
          System.out.println("sms inserction ok, sms deleted");
          status = true;
        }
      }
			srv.disconnect();
		}
		catch (Exception e) { e.printStackTrace(); }
    return status;
  }
  
  /**
    retrive SMS' from serial device
    @return LinkedList of sms
  */
  private LinkedList getSMS() {
    LinkedList msgList = new LinkedList();
		try {
      srv.connect();
			srv.readMessages(msgList, CIncomingMessage.CLASS_ALL);
			srv.disconnect();
		}
		catch (Exception e) {
			e.printStackTrace();
		}   
    return msgList;
  }
  
  /**
    retrives info about the device
  */
  public void info() {
		try {
			srv.connect();
			System.out.println("Mobile Device Information: ");
			System.out.println("	Manufacturer  : " + srv.getDeviceInfo().getManufacturer());
			System.out.println("	Model         : " + srv.getDeviceInfo().getModel());
			System.out.println("	Serial No     : " + srv.getDeviceInfo().getSerialNo());
			System.out.println("	IMSI          : " + srv.getDeviceInfo().getImsi());
			System.out.println("	S/W Version   : " + srv.getDeviceInfo().getSwVersion());
			System.out.println("	Battery Level : " + srv.getDeviceInfo().getBatteryLevel() + "%");
			System.out.println("	Signal Level  :  " + srv.getDeviceInfo().getSignalLevel() + "%");
			srv.disconnect();
		}
		catch (Exception e) {
			e.printStackTrace();
		}    
  }

  public static void main(String[] args) {
    CService srv = new CService("COM5", 9600, "digicom", "pocket");
    xSQL sql = new xSQL("jdbc:mysql://localhost/xsms?user=root&password=texrulez"); 
    xSMS x = new xSMS(srv, sql);
    try {
      srv.setSimPin("0000");
    } catch (Exception e) {e.printStackTrace();}
    x.checkSMS();
  }
}

/**
  describe users info.
*/
class User {
  private String firstname;
  private String secondname;
  private String nickname;
  private String cellNumber;
  private String description;
  private String groups;
  
  public User(String firstname, String secondname, String nickname, String cellNumber, String description, String groups) {
    this.firstname = firstname;
    this.secondname = secondname;
    this.nickname = nickname;
    this.cellNumber = cellNumber;
    this.description = description;
    this.groups = groups;
  }
  
  public User(String cellNumber){
    this.cellNumber = cellNumber;
  }
  
  public String getFirstname(){ return this.firstname; }
  public String getSecondname(){ return this.secondname; }
  public String getNickname() {return this.nickname;}
  public String getNumber(){ return this.cellNumber; }
  public String getDescription(){ return this.description; }
  public String getGroups(){ return this.groups; }
}

/**
  describes a message
*/
class Message {
  private String sender;
  private String date;
  private String text;
    
  public Message(String sender, String date, String text) {
    this.sender = sender;
    this.date = date;
    this.text = text;    
  }
    
  public String getSender() { return this.sender; }
  public String getDate() { return this.date; }
  public String getText() { return this.text; }
}

class xSMSListener implements CSmsMessageListener
{
  public xSQL sql;
  public xSMSListener(xSQL sql){
    this.sql = sql;
  }
  
  public boolean received(CService srv, CIncomingMessage msg)
  {
    boolean status = false;
    System.out.println("nuovo messaggio arrivato:");
    System.out.println(msg);
    if(!sql.numberIsKnown(msg.getOriginator())){
      sql.insertUser(new User(msg.getOriginator()));
    }
    if(sql.insertSMS(msg)==1) {
      try{
        srv.deleteMessage(msg);
      } catch (Exception e){e.printStackTrace();}
      System.out.println("sms inserction ok, sms deleted");
      status = true;
    }
    return status;//sms.checkSMS();
  }
}
