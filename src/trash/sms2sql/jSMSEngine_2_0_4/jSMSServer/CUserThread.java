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
//	jSMSServer GUI Application.
//	This application is based on the old jSMSServer GUI, and provides a general purpose
//		graphical interface. It can be used for a quick-start, if you don't want
//		to mess around with the API itself.
//	Please read jSMSServer.txt for further information.
//

import java.io.*;
import java.util.*;
import java.text.*;
import javax.swing.*;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import org.jsmsengine.*;

class CUserThread extends CMainThread
{
	public CUserThread(jSMSServer jSmsServer, CMainWindow mainWindow, CSettings settings)
	{
		super(jSmsServer, mainWindow, settings);
	}

	public boolean processMessage(CIncomingMessage message) throws Exception
	{
		super.processMessage(message);
		//sendMessage(new COutgoingMessage(message.getOriginator(), "Message received from jSMSServer..."));
		return true;
	}
}
