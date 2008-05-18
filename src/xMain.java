import org.jsmsengine.*;
import java.util.LinkedList;

public class xMain {
  private CService srv;
  private xSQL sql;
  //private xSMS sms;
  //private xSMSListener smsMessageListener;
  private xConfig cfg;
  
  public xMain(CService srv, xSQL sql, xSMS sms, xConfig cfg){
    this.srv = srv;
    this.sql = sql;
    //this.sms = sms;
    this.cfg = cfg;
  }
  
  /**
    restart the application if a bit is set in the db
  */
  public void restart(){
    this.srv.disconnect();
    this.cfg.refresh();
    this.init();
  }
  
  /**
    init
  */
  public void init(){
    cfg.set(xConfig.ISRUNNING,1);
    LinkedList msgList = new LinkedList();
    boolean status = false;
		try {
      System.out.println("Inserisco il PIN");
			srv.setSimPin(cfg.getSimpin());
      srv.connect();
			srv.readMessages(msgList, CIncomingMessage.CLASS_ALL);
      
      System.out.println("Recupero dei messaggi offline");
      for (int i = 0; i < msgList.size(); i ++) {
        CIncomingMessage msg = (CIncomingMessage) msgList.get(i);
        System.out.println(msg);
        if(!sql.numberIsKnown(msg.getOriginator())){
          sql.insertUser(new User(msg.getOriginator()));
        }
        if(sql.insertSMS(msg)==1) {
          srv.deleteMessage(msg);
          System.out.println("Inserzione OK, messaggio cancellato");
          status = true;
        }
      }
      
			// Switch to asynchronous mode.
      System.out.println("Attivazione modalita demone");
			srv.setReceiveMode(CService.RECEIVE_MODE_ASYNC);
			srv.setMessageHandler(new xSMSListener(sql));
    } catch (Exception e) {e.printStackTrace();}
    
    boolean run = true;
    boolean reboot = false;
    boolean isRunning = true;
    while(run){
			// Go to sleep - simulate the asynchronous concept...
			try { Thread.sleep(10000); } catch (Exception e) {e.printStackTrace();}
      
      run = !cfg.test(xConfig.SHUTDOWN);
			System.out.println("DEMONE: Check status: "+run);
      
      if(run && cfg.test(xConfig.REBOOT)){
        System.out.println("Sistema in reboot. Prego attendere.");
        this.restart();
      }
      
      if(run && sql.checkOutgoingMessages()){
        System.out.println("DEMONE: trovati messaggi in uscita: mando il primo.");
        try{
          srv.setSmscNumber("");
          COutgoingMessageWithID message = sql.getFirstOutgoingMessage();
          message.setMessageEncoding(CMessage.MESSAGE_ENCODING_7BIT);
          System.out.println("\ntento di mandare:\n"+message);
          srv.sendMessage((COutgoingMessage)message);
          sql.insertSentSMS(message);
          sql.deleteOutgoingMessage(message.getDBId());
        } catch (Exception e) {e.printStackTrace();}
      }
    }
    System.out.println("Sistema in shutdown. Buona giornata.");
    try {srv.disconnect();}
    catch (Exception e)
		{e.printStackTrace();}
    cfg.set(xConfig.ISRUNNING,0);
    return;
  }
  
  public static String help(){
    return 
      "Uso di questo programma:\n"+
      "java xMain <JDBCUrl> <ConfigID>\n"+
      "esempio:\n"+
      "java xMain jdbc:mysql://localhost/xsms?user=root&password=latua 1"+
      "dove mysql e' la risorsa, localhost l'host, xsms il database, root l'user, latua la password\n\n"+
      "Sistema sviluppato da Emanuele Tessore per Zip Progetti";
  }

  public static void main(String[] args) {
    xConfig cfg;
    if(args.length!=0)
      cfg = new xConfig(args[0],args[1]);
    else
      cfg = new xConfig("jdbc:mysql://localhost/xsms?user=root&password=texrulez",1);
    
    CService srv = new CService(cfg.getComport(), cfg.getBaudrate(), cfg.getGsmDeviceManufacturer(), cfg.getGsmDeviceModel());
    xSQL sql = new xSQL(cfg.getJdbcurl()); 
    xSMS sms = new xSMS(srv, sql);
    new xMain(srv, sql, sms, cfg).init();
    return;
  }
}

