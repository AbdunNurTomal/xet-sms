//      jSMSEngine API.
//      An open-source API package for sending and receiving SMS via a GSM device.
//      Copyright (C) 2002-2006, Thanasis Delenikas, Athens/GREECE
//              Web Site: http://www.jsmsengine.org
//
//      jSMSEngine is a package which can be used in order to add SMS processing
//              capabilities in an application. jSMSEngine is written in Java. It allows you
//              to communicate with a compatible mobile phone or GSM Modem, and
//              send / receive SMS messages.
//
//      jSMSEngine is distributed under the LGPL license.
//
//      This library is free software; you can redistribute it and/or
//              modify it under the terms of the GNU Lesser General Public
//              License as published by the Free Software Foundation; either
//              version 2.1 of the License, or (at your option) any later version.
//      This library is distributed in the hope that it will be useful,
//              but WITHOUT ANY WARRANTY; without even the implied warranty of
//              MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//              Lesser General Public License for more details.
//      You should have received a copy of the GNU Lesser General Public
//              License along with this library; if not, write to the Free Software
//              Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package org.jsmsengine;

import java.util.logging.*;

/**
       AT handler for SonyEricsson 2003, 2004 and 2005 series devices.
*/
public class CATHandler_SonyEricsson extends CATHandler
{
       public CATHandler_SonyEricsson(CSerialDriver serialDriver, Logger log)
       {
               super(serialDriver, log);
       }

       public boolean  disableIndications() throws Exception
       {
               /* work out what command to send to disable indications, the phone
                * itself will tell us what it supports */
               String atDisableIndications = "AT\r";

               // check for valid AT+CNMI values
               serialDriver.send("AT+CNMI=?\r");

               // what comes back?
               String cnmiTestResponse = serialDriver.getResponse();

               if (cnmiTestResponse.toUpperCase().indexOf("+CNMI: (2)") >= 0)
               {
                       atDisableIndications = "AT+CNMI=2,0,0,0\r";
               }
               else if (cnmiTestResponse.toUpperCase().indexOf("+CNMI: (3)") >= 0)
               {
                       atDisableIndications = "AT+CNMI=3,0,0,0\r";
               }
               else
               {
                       // we don't know what to send
                       return false;
               }
               serialDriver.send(atDisableIndications);

               return (serialDriver.getResponse().equalsIgnoreCase("OK\r"));
       }

       /*
        * SonyEricsson's documentation on which phones support which
        * variant of AT+CFUN is inaccurate, so the safest thing is
        * to ask the phone what it supports.
        */
       public void reset() throws Exception
       {
               String atCFUN = "AT+CFUN=1\r";

               // check what is supported
               serialDriver.send("AT+CFUN=?\r");
               String cfunTestResponse = serialDriver.getResponse();
               if (cfunTestResponse.matches("\\+CFUN: \\([^)]+\\),\\([^)]+\\)"))
               {
                       atCFUN = "AT+CFUN=1,1\r";
               }

               serialDriver.send(atCFUN);
               try { Thread.sleep(10000); } catch (Exception e) {}
               serialDriver.getResponse();
       }

       // SonyEricssons require an additional <CR> after the <26> that indicates the end of the PDU
       public boolean sendMessage(int size, String pdu) throws Exception
       {
               int responseRetries, errorRetries;
               String response;
               boolean sent;

               errorRetries = 0;
               while (true)
               {
                       responseRetries = 0;
                       serialDriver.send(CUtils.substituteSymbol("AT+CMGS=\"{1}\"\r", "\"{1}\"", "" + size));
                       Thread.sleep(100);
                       while (!serialDriver.dataAvailable())
                       {
                               responseRetries ++;
                               if (responseRetries == 4) throw new NoResponseException();
                               log.log(Level.SEVERE, "CATHandler_SonyEricsson().SendMessage(): Still waiting for response (I) (" + responseRetries + ")...");
                               Thread.sleep(5000);
                       }
                       responseRetries = 0;
                       serialDriver.clearBuffer();
                       serialDriver.send(pdu);
                       serialDriver.send((char) 26);
                       serialDriver.send((char) 13);   // special for SonyEricsson
                       response = serialDriver.getResponse();
                       while (response.length() == 0)
                       {
                               responseRetries ++;
                               if (responseRetries == 4)  throw new NoResponseException();
                               log.log(Level.SEVERE, "CATHandler_SonyEricsson().SendMessage(): Still waiting for response (II) (" + responseRetries + ")...");
                               response = serialDriver.getResponse();
                       }
                       if (response.indexOf("OK\r") >= 0)
                       {
                               sent = true;
                               break;
                       }
                       else if (response.indexOf("CMS ERROR:") >= 0)
                       {
                               errorRetries ++;
                               if (errorRetries == 4)
                               {
                                       log.log(Level.SEVERE, "GSM CMS Errors: Quit retrying, message lost...");
                                       sent = false;
                                       break;
                               }
                               else log.log(Level.SEVERE, "GSM CMS Errors: Possible collision, retrying...");
                       }
                       else sent = false;
               }
               return sent;
       }
}
