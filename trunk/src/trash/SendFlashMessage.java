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
//	SendMessage.java - Sample application.
//
//		This application shows you the basic procedure needed for sending
//		an SMS messages from your GSM device.
//

//	Include the necessary package.
import org.jsmsengine.*;

class SendFlashMessage
{
	public static void main(String[] args)
	{
		int status;

		CService srv = new CService("COM5", 9600, "digicom", "pocket");

		System.out.println();
		System.out.println("SendMessage(): sample application.");
		System.out.println("  Using " + srv._name + " v" + srv._version);
		System.out.println();
		try
		{
			srv.setSimPin("0000");
			srv.connect();

			srv.setSmscNumber("");

			System.out.println("Mobile Device Information: ");
			System.out.println("	Manufacturer  : " + srv.getDeviceInfo().getManufacturer());
			System.out.println("	Model         : " + srv.getDeviceInfo().getModel());
			System.out.println("	Serial No     : " + srv.getDeviceInfo().getSerialNo());
			System.out.println("	IMSI          : " + srv.getDeviceInfo().getImsi());
			System.out.println("	S/W Version   : " + srv.getDeviceInfo().getSwVersion());
			System.out.println("	Battery Level : " + srv.getDeviceInfo().getBatteryLevel() + "%");
			System.out.println("	Signal Level  : " + srv.getDeviceInfo().getSignalLevel() + "%");

			COutgoingMessage msg = new COutgoingMessage("+393493526168", "Message from jSMSEngine API.");
			msg.setMessageEncoding(CMessage.MESSAGE_ENCODING_7BIT);

			msg.setFlashSms(true);

			srv.sendMessage(msg);

			srv.disconnect();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}
}
