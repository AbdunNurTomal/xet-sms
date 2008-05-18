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
import java.net.*;
import java.sql.*;

import org.jsmsengine.*;

class CSettings
{
	private static final String CONFIG_FILE = "jSMSServer.conf";

	private CMainWindow mainWindow;

	private CGeneralSettings generalSettings;
	private CPhoneSettings phoneSettings;
	private CDatabaseSettings databaseSettings;
	private CSerialDriverSettings serialDriverSettings;

	public CSettings()
	{
		generalSettings = new CGeneralSettings();
		phoneSettings = new CPhoneSettings();
		databaseSettings = new CDatabaseSettings();
		serialDriverSettings = new CSerialDriverSettings();
	}

	public void setMainWindow(CMainWindow mainWindow) { this.mainWindow = mainWindow; }

	public void loadConfiguration() throws Exception { loadConfiguration(CONFIG_FILE); } 
	public void loadConfiguration(String configFile) throws Exception
	{
		Properties props = new Properties();

		props.load(new FileInputStream(configFile));
		generalSettings.setGui(props.getProperty("general.gui", "yes").equalsIgnoreCase("yes"));
		generalSettings.setRawInLog(props.getProperty("general.raw_in_log", null));
		generalSettings.setRawOutLog(props.getProperty("general.raw_out_log", null));
		phoneSettings.setManufacturer(props.getProperty("phone.manufacturer", "generic"));
		phoneSettings.setModel(props.getProperty("phone.model", "generic"));
		phoneSettings.setPeriodInterval(Integer.parseInt(props.getProperty("phone.interval", "15")));
		phoneSettings.setDeleteAfterProcessing(props.getProperty("phone.delete_after_processing", "no").equalsIgnoreCase("yes"));
		phoneSettings.setPhoneBookFile(props.getProperty("phone.phone_book", null));
		phoneSettings.setXmlInQueue(props.getProperty("phone.xml_in_queue", null));
		phoneSettings.setXmlOutQueue(props.getProperty("phone.xml_out_queue", null));
		phoneSettings.setBatchIncoming(Integer.parseInt(props.getProperty("phone.batch_incoming", "-1")));
		phoneSettings.setBatchOutgoing(Integer.parseInt(props.getProperty("phone.batch_outgoing", "-1")));
		phoneSettings.setSmscNumber(props.getProperty("phone.smsc_number", null));
		phoneSettings.setMessageEncoding(props.getProperty("phone.message_encoding", "7bit"));
		phoneSettings.setSimPin(props.getProperty("phone.sim_pin", null));
		phoneSettings.setForwardNumber(props.getProperty("props.forward_number", null));

		databaseSettings.setEnabled(props.getProperty("database.enabled", "no").equalsIgnoreCase("yes"));
		if (databaseSettings.getEnabled())
		{
			if (props.getProperty("database.type", "").equalsIgnoreCase("sql92")) databaseSettings.setType(CDatabaseSettings.DB_TYPE_SQL92);
			else if (props.getProperty("database.type", "").equalsIgnoreCase("mysql")) databaseSettings.setType(CDatabaseSettings.DB_TYPE_MYSQL);
			else if (props.getProperty("database.type", "").equalsIgnoreCase("mssql")) databaseSettings.setType(CDatabaseSettings.DB_TYPE_MSSQL);
			else databaseSettings.setType(CDatabaseSettings.DB_TYPE_SQL92);
			databaseSettings.setUrl(props.getProperty("database.url", "url"));
			databaseSettings.setDriver(props.getProperty("database.driver", "driver"));
			databaseSettings.setUsername(props.getProperty("database.username", "username"));
			databaseSettings.setPassword(props.getProperty("database.password", "password"));
		}

		serialDriverSettings.setPort(props.getProperty("serial.port", "com1"));
		serialDriverSettings.setBaud(Integer.parseInt(props.getProperty("serial.baud", "19200")));
	}

	public CGeneralSettings getGeneralSettings() { return generalSettings; }
	public CPhoneSettings getPhoneSettings() { return phoneSettings; }
	public CDatabaseSettings getDatabaseSettings() { return databaseSettings; }
	public CSerialDriverSettings getSerialDriverSettings() { return serialDriverSettings; }

	class CGeneralSettings
	{
		private boolean guiEnabled = true;
		private boolean logEnabled = false;
		private RandomAccessFile rawInLog = null, rawOutLog = null;

		public CGeneralSettings()
		{
		}

		public void setGui(boolean guiOn) { this.guiEnabled = guiOn; }

		public void setRawInLog(String filename)
		{
			if (filename != null)
			{
				try
				{
					rawInLog = new RandomAccessFile(filename, "rw");
					rawInLog.seek(rawInLog.length());
				}
				catch (Exception e)
				{
					rawInLog = null;
				}
			}
			else rawInLog = null;
		}

		public void setRawOutLog(String filename)
		{
			if (filename != null)
			{
				try
				{
					rawOutLog = new RandomAccessFile(filename, "rw");
					rawOutLog.seek(rawOutLog.length());
				}
				catch (Exception e)
				{
					rawOutLog = null;
				}
			}
			else rawOutLog = null;
		}

		public boolean getGui() { return guiEnabled; }
		public boolean isRawInLogEnabled() { return (rawInLog != null); }
		public boolean isRawOutLogEnabled() { return (rawOutLog != null); }

		public void rawInLog(CIncomingMessage message)
		{
			if (rawInLog != null)
			{
				try
				{
					rawInLog.writeBytes(message.getOriginator());
					rawInLog.writeBytes("\t");
					rawInLog.writeBytes(date2LogString(message.getDate()));
					rawInLog.writeBytes("\t");
					rawInLog.writeBytes(message.getText());
					rawInLog.writeBytes("\n");
				}
				catch (Exception e) {e.printStackTrace();}
			}
		}

		public void rawOutLog(COutgoingMessage message)
		{
			if (rawOutLog != null)
			{
				try
				{
					rawOutLog.writeBytes(message.getRecipient());
					rawOutLog.writeBytes("\t");
					rawOutLog.writeBytes(date2LogString(message.getDispatchDate()));
					rawOutLog.writeBytes("\t");
					rawOutLog.writeBytes(message.getText());
					rawOutLog.writeBytes("\n");
				}
				catch (Exception e) {e.printStackTrace();}
			}
		}

		private String date2LogString(java.util.Date date)
		{
			String line="";
			Calendar cal = Calendar.getInstance();

			if (date == null) return "* N/A *";
			cal.setTime(date);
			line = line + cal.get(Calendar.YEAR);
			line = line + (((cal.get(Calendar.MONTH) + 1) <= 9) ? "0" + (cal.get(Calendar.MONTH) + 1) : "" + (cal.get(Calendar.MONTH) + 1));
			line = line + ((cal.get(Calendar.DAY_OF_MONTH) <= 9) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : "" + cal.get(Calendar.DAY_OF_MONTH));
			line = line + ((cal.get(Calendar.HOUR_OF_DAY) <= 9) ? "0" + cal.get(Calendar.HOUR_OF_DAY) : "" + cal.get(Calendar.HOUR_OF_DAY));
			line = line + ((cal.get(Calendar.MINUTE) <= 9) ? "0" + cal.get(Calendar.MINUTE) : "" + cal.get(Calendar.MINUTE));
			line = line + ((cal.get(Calendar.SECOND) <= 9) ? "0" + cal.get(Calendar.SECOND) : "" + cal.get(Calendar.SECOND));
			return line;
		}
	}

	class CPhoneSettings
	{
		private String manufacturer;
		private String model;
		private int periodInterval;
		private boolean deleteAfterProcessing;
		private String phoneBookFile;
		private String xmlInQueue;
		private String xmlOutQueue;
		private int batchIncoming;
		private int batchOutgoing;
		private String smscNumber;
		private String messageEncoding;
		private String simPin;
		private String forwardNumber;

		public CPhoneSettings()
		{
		}

		public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
		public void setModel(String model) { this.model = model; }
		public void setPeriodInterval(int interval) { periodInterval = interval; }
		public void setDeleteAfterProcessing(boolean deleteAfterProcessing) { this.deleteAfterProcessing = deleteAfterProcessing; }
		public void setPhoneBookFile(String file) { this.phoneBookFile = file; }
		public void setXmlInQueue(String xmlInQueue) { this.xmlInQueue = xmlInQueue; }
		public void setXmlOutQueue(String xmlOutQueue) { this.xmlOutQueue = xmlOutQueue; }
		public void setBatchIncoming(int batchIncoming) { this.batchIncoming = batchIncoming; }
		public void setBatchOutgoing(int batchOutgoing) { this.batchOutgoing = batchOutgoing; }
		public void setSmscNumber(String number) { this.smscNumber = number; }
		public void setMessageEncoding(String messageEncoding) { this.messageEncoding = messageEncoding; }
		public void setSimPin(String simPin) { this.simPin = simPin; }
		public void setForwardNumber(String forwardNumber) { this.forwardNumber = forwardNumber; }

		public String getManufacturer() { return manufacturer; }
		public String getModel() { return model; }
		public int getPeriodInterval() { return periodInterval * 1000; }
		public boolean getDeleteAfterProcessing() { return deleteAfterProcessing; }
		public String getPhoneBookFile() { return phoneBookFile; }
		public String getXmlInQueue() { return xmlInQueue; }
		public String getXmlOutQueue() { return xmlOutQueue; }
		public int getBatchIncoming() { return (batchIncoming == -1 ? 32 : batchIncoming); }
		public int getBatchOutgoing() { return (batchOutgoing == -1 ? 32 : batchOutgoing); }
		public String getSmscNumber() { return smscNumber; }
		public String getMessageEncoding() { return messageEncoding; }
		public String getSimPin() { return simPin; }
		public String getForwardNumber() { return forwardNumber; }
	}

	class CDatabaseSettings
	{
		public static final int DB_TYPE_SQL92 = 1;
		public static final int DB_TYPE_MYSQL = 2;
		public static final int DB_TYPE_MSSQL = 3;

		private boolean enabled;
		private int type;
		private String url;
		private String driver;
		private String username;
		private String password;

		public CDatabaseSettings()
		{
			enabled = false;
		}

		public void setEnabled(boolean enabled) { this.enabled = enabled; }
		public void setType(int type) { this.type = type; }
		public void setUrl(String url) { this.url = url; }
		public void setDriver(String driver) { this.driver = driver; }
		public void setUsername(String username) { this.username = username; }
		public void setPassword(String password) { this.password = password; }

		public boolean getEnabled() { return enabled; }
		public int getType() { return type; }
		public String getUrl() { return url; }
		public String getDriver() { return driver; }
		public String getUsername() { return username; }
		public String getPassword() { return password; }
	}

	class CSerialDriverSettings
	{
		private String port;
		private int baud;

		public void setPort(String port) { this.port = port;}
		public void setBaud(int baud) { this.baud = baud; }

		public String getPort() { return port; }
		public int getBaud() { return baud; }
	}
}
