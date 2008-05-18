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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

import org.jsmsengine.*;

class jSMSServer extends Thread
{
	private CSettings settings;
	private CMainWindow mainWindow;
	private CMainThread service;

	public void initialize() throws Exception
	{
		settings = new CSettings();

		settings.loadConfiguration();

		if (settings.getGeneralSettings().getGui())
		{
			mainWindow = new CMainWindow(this, settings);
			mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainWindow.setVisible(true);

			service = new CUserThread(this, mainWindow, settings);
	
			mainWindow.setRawInLog(settings.getGeneralSettings().isRawInLogEnabled());
			mainWindow.setRawOutLog(settings.getGeneralSettings().isRawOutLogEnabled());
			mainWindow.setInterfaceXML((settings.getPhoneSettings().getXmlInQueue() != null) || (settings.getPhoneSettings().getXmlOutQueue() != null));
			mainWindow.setInterfaceDB(settings.getDatabaseSettings().getEnabled());
		}
		else
		{
			mainWindow = null;

			System.out.println(stripHtml(CConstants.ABOUT_VERSION));
			System.out.println(stripHtml(CConstants.ABOUT_BY));
			System.out.println(stripHtml(CConstants.ABOUT_WEBPAGE));
			System.out.println(stripHtml(CConstants.ABOUT_OTHER));
			System.out.println("");
			System.out.println(CConstants.TEXT_CONSOLE);
			System.out.println("");

			service = new CUserThread(this, null, settings);
			service.initialize();
			service.connect(true);
		}

		Runtime.getRuntime().addShutdownHook(new CShutdown());
	}

	public void run()
	{
		while (true) try { sleep(5000); } catch (Exception e) {}
	}

	public class CShutdown extends Thread
	{
		CMainThread mobile;

		public void run()
		{
		}
	}

	public String stripHtml(String s)
	{
		String o;

		o = s.replace("<html>", "");
		o = o.replace("</html>", "");
		o = o.replace("<b>", "");
		o = o.replace("</b>", "");
		o = o.replace("<h1>", "");
		o = o.replace("</h1>", "");
		o = o.replace("<br>", "");
		return o;
	}

	public static void main(String[] args)
	{
		try
		{
			jSMSServer app = new jSMSServer();
			app.initialize();
			app.setPriority(MIN_PRIORITY);
			app.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
