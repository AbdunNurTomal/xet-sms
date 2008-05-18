//	jSMSEngine API.
//	An open-source API package for sending and receiving SMS via a GSM device.
//	Copyright (C) 2002-2006, Thanasis Delenikas, Athens/GREECE
//		Web Site: http://www.jsmsengine.org
//
//	jSMSEngine is distributed under the GPL license.
//
//	This program is free software; you can redistribute it and/or
//	modify it under the terms of the GNU General Public License
//	version 2 as published by the Free Software Foundation
//
//	This program is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//
//	You should have received a copy of the GNU General Public License
//	along with this program; if not, write to the Free Software
//	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
//

//
//	ReadMessages.java - Sample application.
//
//		This application shows you the basic procedure needed for reading
//		SMS messages from your GSM device, in synchronous mode.
//

import org.jsmsengine.*;

import java.util.*;

class ReadMessages
{
	public static void main(String[] args)
	{
		int status;
		LinkedList msgList = new LinkedList();

		CService srv = new CService("COM4", 9600, "digicom", "pocket");

		System.out.println();
		System.out.println("ReadMessages(): sample application.");
		System.out.println("  Using " + srv._name + " v" + srv._version);
		System.out.println();

		try
		{
			srv.setSimPin("240882");
			srv.connect();

			System.out.println("Mobile Device Information: ");
			System.out.println("	Manufacturer  : " + srv.getDeviceInfo().getManufacturer());
			System.out.println("	Model         : " + srv.getDeviceInfo().getModel());
			System.out.println("	Serial No     : " + srv.getDeviceInfo().getSerialNo());
			System.out.println("	IMSI          : " + srv.getDeviceInfo().getImsi());
			System.out.println("	S/W Version   : " + srv.getDeviceInfo().getSwVersion());
			System.out.println("	Battery Level : " + srv.getDeviceInfo().getBatteryLevel() + "%");
			System.out.println("	Signal Level  :  " + srv.getDeviceInfo().getSignalLevel() + "%");

			// Depending on your specific model, you may have to uncomment one of the following
			// lines in order to be able to read your messages. Different phones store incoming
			// messages to either SIM or memory. Default is SIM for most phones.
			//
			//srv.setStorageMem();
			//srv.setStorageSIM();

			srv.readMessages(msgList, CIncomingMessage.CLASS_ALL);
			for (int i = 0; i < msgList.size(); i ++)
			{
				CIncomingMessage msg = (CIncomingMessage) msgList.get(i);
				System.out.println(msg);
			}

			srv.disconnect();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}
}
