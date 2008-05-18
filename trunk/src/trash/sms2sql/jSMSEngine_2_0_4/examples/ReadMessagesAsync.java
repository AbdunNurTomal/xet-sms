//	jSMSEngine API.
//	An open-source API package for sending and receiving SMS via a GSM device.
//	Copyright (C) 2002-2006, Thanasis Delenikas, Athens/GREECE
//		Web Site: http://www.jsmsengine.org
//
//	jSMSEngine is a package which can be used in order to add SMS processing
//		capabilities in an application. jSMSEngine is written in Java. It allows you
//		to communicate with a compatible mobile phone or GSM Modem, and
//		send / receive SMS messages.
//
//	jSMSEngine is distributed under the LGPL license.
//
//	This library is free software; you can redistribute it and/or
//		modify it under the terms of the GNU Lesser General Public
//		License as published by the Free Software Foundation; either
//		version 2.1 of the License, or (at your option) any later version.
//	This library is distributed in the hope that it will be useful,
//		but WITHOUT ANY WARRANTY; without even the implied warranty of
//		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//		Lesser General Public License for more details.
//	You should have received a copy of the GNU Lesser General Public
//		License along with this library; if not, write to the Free Software
//		Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

//
//	ReadMessagesAsync.java - Sample application.
//
//		This application shows you the basic procedure needed for reading
//		SMS messages from your GSM device.
//		This example is about ASYNCHRONOUS reading, utilizing a virtual message handler.
//

//	Include the necessary packages.
import org.jsmsengine.*;
import java.util.*;

// This is the new proposed method of using the ASYNC service.
class ReadMessagesAsync
{
	CService srv;
	CMessageListener smsMessageListener;

	class CMessageListener implements CSmsMessageListener
	{
		public boolean received(CService service, CIncomingMessage message)
		{
			// Display the message received...
			System.out.println("*** Msg: " + message.getText());
	
			// and send a "thank you!" reply!
			try
			{
				//service.sendMessage(new COutgoingMessage(message.getOriginator(), "Thank you!"));
			}
			catch (Exception e)
			{
				System.out.println("Could not send reply message!");
				e.printStackTrace();
			}
	
			// Return false to leave the message in memory - otherwise return true to delete it.
			return false;
		}
	}

	public ReadMessagesAsync()
	{
	}

	public void doIt()
	{
		srv = new CService("COM1", 9600, "Nokia", "6310i");
		smsMessageListener = new CMessageListener();

		System.out.println();
		System.out.println("ReadMessagesAsync(): sample application.");
		System.out.println("  Using " + srv._name + " " + srv._version);
		System.out.println();
		try
		{
			srv.setSimPin("0000");
			srv.connect();

			System.out.println("Mobile Device Information: ");
			System.out.println("	Manufacturer  : " + srv.getDeviceInfo().getManufacturer());
			System.out.println("	Model         : " + srv.getDeviceInfo().getModel());
			System.out.println("	Serial No     : " + srv.getDeviceInfo().getSerialNo());
			System.out.println("	IMSI          : " + srv.getDeviceInfo().getImsi());
			System.out.println("	S/W Version   : " + srv.getDeviceInfo().getSwVersion());
			System.out.println("	Battery Level : " + srv.getDeviceInfo().getBatteryLevel() + "%");
			System.out.println("	Signal Level  :  " + srv.getDeviceInfo().getSignalLevel() + "%");

			System.out.println();
			System.out.println("I will wait for a period of 60 secs for incoming messages...");

			// Switch to asynchronous mode.
			srv.setReceiveMode(CService.RECEIVE_MODE_ASYNC);
			srv.setMessageHandler(smsMessageListener);

			// Go to sleep - simulate the asynchronous concept...
			try { Thread.sleep(60000); } catch (Exception e) {}
			System.out.println("Timeout period expired, exiting...");

			srv.disconnect();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		ReadMessagesAsync example = new ReadMessagesAsync();
		example.doIt();
	}
}
