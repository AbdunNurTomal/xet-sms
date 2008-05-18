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

package org.jsmsengine;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.text.*;

/**
	This class provides all the functionality of jSMSEngine API to the developer.
	<br><br>
	The class CService provides all the interface routines to jSMSEngine. It
	is responsible for initialization of the communication with the GSM device,
	reading and sending messages, setting the phonebook.
	<br><br>
	The sequence of actions that need to be done are:
	<ul>
		<li>Call connect() to connect with the GSM device.</li>
		<li>Call sendMessage(), or readMessages() to send or receive messages
				from the device. Call deleteMessage() to delete a message from
				the device's memory.</li>
		<li>Call refreshDeviceInfo() to get updated GSM device specific information.</li>
		<li>Call disconnect() to disconnect from the GSM device.</li>
	</ul>
	<br>
*/
public class CService
{
	/**
		Internal Software Name.
	*/
	public static final String _name = "jSMSEngine API";
	/**
		Version.
	*/
	public static final String _version = "2.0.4";
	/**
		Release Date.
	*/
	public static final String _reldate = "Jan 18, 2006";

	/**
		Logging facilities.
	*/
	private static Logger log = Logger.getLogger("org.jsmsengine");

	/**
		Preferred Message Storage constants.
	*/
		public static final int SMS_STORAGE_SIM = 1;

	/**
		Preferred Message Storage constants.
	*/
		public static final int SMS_STORAGE_MEM = 2;

	/**
		Receive modes: Synchronous and Ascynchronous.
	*/
	public static final int RECEIVE_MODE_SYNC = 1;
	/**
		Receive modes: Synchronous and Ascynchronous.
	*/
	public static final int RECEIVE_MODE_ASYNC = 2;

	/**
		Default value for information that is not reported by the GSM device. 
	*/
	public static final String VALUE_NOT_REPORTED = "* N/A *";

	/**
		Maximum number of retries when a message failes to be sent (various CMS errors)
	*/
	public static final int CMS_ERRORS_MAX_RETRIES = 5;
	public static final int CMS_ERRORS_DELAY = 5000;

	public static final int MAX_SMS_LEN_7BIT = 160;
	public static final int MAX_SMS_LEN_8BIT = 140;
	public static final int MAX_SMS_LEN_UNICODE = 70;

	/**
		The SMSC number (if specifically given).
	*/
	private String smscNumber;

	/**
		The PIN number.
	*/
	private String simPin;

	/**
		Receive Mode: Synchronous or Asynchronous.
	*/
	private int receiveMode;

	/**
		AT Commands' Handler.
	*/
	private CATHandler atHandler;

	private CSerialDriver serialDriver;
	private boolean connected;
	private CPhoneBook phoneBook;
	private CDeviceInfo deviceInfo;

	private CReceiveThread receiveThread;
	private CSmsMessageListener messageHandler;

	/**
		Default constructor of the class.

		@param	port	the serial port where the GSM device is connected (e.g. "com1"). 
		@param	baud	the connection speed (i.e. 9600, 19200 etc).
		@param	gsmDeviceManufacturer	The manufacturer of your device, i.e. Nokia, Siemens, etc.
		@param	gsmDeviceModel	The model of your device, i.e. 6310i, C55, V600 etc.

		<br><br>
		Notes:
		<ul>
			<li>The manufacturer / model combination is used for accessing the correct AT handler for
					your device. If there is no specific handler developed, jSMSEngine will fall back and
					use the generic AT handler. This may or may not work, depending on the
					peculiarities of your device. If you wish to create a custom handler, please look
					at the specific section of the documentation pages.</li>
			<li>Use one of the standard values for baud. Most GSM phones work well
					at 9600 or 19200. Most dedicated GSM modems may work well up to
					115200. The connection speed is not that important to the speed
					at which jSMSEngine processes messages.</li>
		</ul>
	*/
	public CService(String port, int baud, String gsmDeviceManufacturer, String gsmDeviceModel)
	{
		smscNumber = null;
		simPin = null;

		connected = false;
		serialDriver = new CSerialDriver(port, baud, log);
		phoneBook = new CPhoneBook();
		deviceInfo = new CDeviceInfo();

		if ((System.getProperty("jsmsengine.debug") != null) && (System.getProperty("jsmsengine.debug").equalsIgnoreCase("true")))
		{
			log.setLevel(Level.FINEST);
			log.log(Level.INFO, "jSMSEngine:" + _name + " / " + _version + " / " + _reldate);
			log.log(Level.INFO, "	JRE Version: " + System.getProperty("java.version"));
			log.log(Level.INFO, "	JRE Impl Version: " + System.getProperty("java.vm.version"));
			log.log(Level.INFO, "	Class Path: " + System.getProperty("java.class.path"));
			log.log(Level.INFO, "	Library Path: " + System.getProperty("java.library.path"));
			log.log(Level.INFO, "	O/S: " + System.getProperty("os.name") + " / " + System.getProperty("os.arch") + " / " + System.getProperty("os.version"));
		}
		else log.setLevel(Level.SEVERE);

		// Which AT Handler should I use???
		if (gsmDeviceManufacturer.equalsIgnoreCase("Nokia"))
		{
			atHandler = new CATHandler_Nokia(serialDriver, log);
			log.log(Level.INFO, "jSMSEngine: Using Nokia(Generic) AT handler.");
		}
		else if (gsmDeviceManufacturer.equalsIgnoreCase("Wavecom"))
		{
			atHandler = new CATHandler_Wavecom(serialDriver, log);
			log.log(Level.INFO, "jSMSEngine: Using Wavecom(Generic) AT handler.");
		}
		else if (gsmDeviceManufacturer.equalsIgnoreCase("Siemens"))
		{
			atHandler = new CATHandler_Siemens(serialDriver, log);
			log.log(Level.INFO, "jSMSEngine: Using Siemens AT(Generic) handler.");
		}
		else if (gsmDeviceManufacturer.equalsIgnoreCase("SonyEricsson"))
		{
			atHandler = new CATHandler_SonyEricsson(serialDriver, log);
			log.log(Level.INFO, "jSMSEngine: Using SonyEricsson(Generic) AT handler.");
		}
		else
		{
			atHandler = new CATHandler(serialDriver, log);
			log.log(Level.INFO, "jSMSEngine: Using generic AT handler.");
		}
		
		receiveMode = RECEIVE_MODE_SYNC;
		receiveThread = null;
	}

	/**
		Returns TRUE if the API is connected to the GSM device.

		@return  TRUE if the API is connected to the GSM device.
	*/
	public boolean getConnected() { return connected; }

	/**
		Returns a CDeviceInfo object that holds information about the GSM
		device in use.

		@return  a CDeviceInfo object.
		@see	CDeviceInfo
	*/
	public CDeviceInfo getDeviceInfo() { return deviceInfo; }

	/**
		Sets the Short Message Service Center (SMSC) number. Please use international format.
		If you don't want to set the SMSC and use the one defined in your GSM device, use an
		empty string parameter. Another way to do the same, is to pass a null parameter. Some
		phones may prefer one way or the other - please test your phone.

		@param	smscNumber	the SMSC number.
	*/
	public void setSmscNumber(String smscNumber) { this.smscNumber = smscNumber; }

	/**
		Returns the Short Message Service Center (SMSC) number you have previously
		defined with setSmscNumber().

		@return  the SMSC number.
	*/
	public String getSmscNumber() { return smscNumber; }

	/**
		Sets the SIM pin number. This is used if and when the GSM device asks for it. If you set it to
		null, then the API does not give any PIN to the device (in order to avoid locking it up), and
		returns ERR_SIM_PIN_ERROR.

		@param	simPin	the SIM pin number.
	*/
	public void setSimPin(String simPin) { this.simPin = simPin; }

	public void setMessageHandler(CSmsMessageListener messageHandler) { this.messageHandler = messageHandler; }

	/**
		Returns the SIM pin number.

		@return  the SIM pin number.
	*/
	public String getSimPin() { return simPin; }

	/**
		Sets the reception mode.
		There are two reception modes, the synchronous and the asynchronous.
		In synchronous mode, you should call readMessages() function on demand,
		where you want to check for new messages. In asynchronous mode, the engine
		automatically calls the received() method (which you <strong>should</strong>
		override) for every received message.
		<br>By default, the reception mode is the synchronous one.

		@param	receiveMode	the reception mode (one of values RECEIVE_MODE_ASYNC, RECEIVE_MODE_SYNC).
		@see	CService#getReceiveMode()
	*/
	public void setReceiveMode(int receiveMode) { this.receiveMode = receiveMode; }

	/**
		Returns the reception mode.

		@return	the reception mode (one of values RECEIVE_MODE_ASYNC, RECEIVE_MODE_SYNC).
		@see	CService#setReceiveMode(int)
	*/
	public int getReceiveMode() { return receiveMode; }

	/**
		Loads the phonebook. The phonebook is an XML file containing associations of name
		and phone numbers.
		<br><br>
		<strong>The phonebook is optional.</strong> 

		@param	phoneBookFile	The XML full-path name which keeps the phonebook.
		@see	CPhoneBook
		@see	CService#sendMessage(COutgoingMessage)
		@see	CService#sendMessage(LinkedList)
	*/
	public void setPhoneBook(String phoneBookFile) throws Exception
	{
		phoneBook.load(phoneBookFile);
	}

	protected CSmsMessageListener getMessageHandler() { return messageHandler; }

	/**
		Connects to the GSM device. Opens the serial port, and sends the appropriate
		AT commands to initialize the operation mode of the GSM device. Retrieves
		information about the GSM device.
		Notes:
		<br>
		<ul>
			<li>The GSM device specific information (read by the call to refreshDeviceInfo()
					function is called once from this method. Since some information changes
					with time (such as battery or signal level), its your responsibility to
					call refreshDeviceInfo() periodically in order to have the latest information.
					Otherwise, you will get the information snapshot taken at the time
					of the initial connection.
			</li>
		</ul>

		@throws  AlreadyConnectedException
		@throws NoPinException
		@throws InvalidPinException
		@throws NoPduSupportException
		@throws CannotDisableIndicationsException
		@see	CDeviceInfo
		@see	CService#refreshDeviceInfo()
		@see	CService#disconnect()
	*/
	public void connect() throws Exception
	{
		if (getConnected()) throw new AlreadyConnectedException();
		else
			try
			{
				serialDriver.open();
				atHandler.reset();
				atHandler.sync();
				if (atHandler.isAlive())
				{
					if (atHandler.waitingForPin())
					{
						if (getSimPin() == null) throw new NoPinException();
						else if (!atHandler.enterPin(getSimPin())) throw new InvalidPinException();
					}
					atHandler.echoOff();
					atHandler.init();
					atHandler.setVerboseErrors();
					if (!atHandler.setPduMode()) throw new NoPduSupportException();
					if (!atHandler.disableIndications()) throw new CannotDisableIndicationsException();
					connected = true;
					refreshDeviceInfo();

					receiveThread = new CReceiveThread();
					receiveThread.start();
				}
				else throw new NotConnectedException("GSM device is not responding.");
			}
			catch (AlreadyConnectedException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				disconnect();
				throw e;
			}
	}

	/**
		Disconnects to the GSM device. Closes the serial port. 

		@see	CService#connect()
	*/
	public void disconnect()
	{
		if (receiveThread != null)
		{
			receiveThread.killMe();
			while (!receiveThread.killed()) try { Thread.sleep(1000); } catch (Exception e) {}
			receiveThread = null;
		}
		try { serialDriver.close(); } catch (Exception e) {}
		connected = false;
	}

	/**
		Reads SMS from the GSM device's memory. You should call this method when
		you want to read messages from the device. In the MessageList object you
		pass, the method will add objects of type CIncomingMessage, as many of them
		as the messages pending to be read. The class defines which types of messages
		should be read.
		<br><br>
		<strong>Notes:</strong>
		<ul>
			<li>The method <strong>does not delete</strong> the messages it reads
				from the GSM device. It's your responsibility to delete them, if you
				don't want them. Otherwise, on the next call of this function you
				will read the same messages.</li>
		</ul>

		@param	messageList	a LinkedList object which will be loaded with the messages.
		@param	messageClass	one of the CLASS_* values defined in CIncomingMessage class which
				define what type of messages are to be read.
		@throws NotConnectedException
		@see	CIncomingMessage
		@see	CService#deleteMessage(CIncomingMessage)
		@see	CService#deleteMessage(int)
	*/
	public void readMessages(LinkedList messageList, int messageClass) throws Exception
	{
		int i, j, memIndex;
		String response, line, sms, temp, originator, text, pdu;
		String day, month, year, hour, min, sec;
		BufferedReader reader;
		Date sentDate;
		Calendar cal = Calendar.getInstance();

		if (getConnected())
		{
			atHandler.switchToCmdMode();
			response = atHandler.listMessages(messageClass);
			reader = new BufferedReader(new StringReader(response));
			line = reader.readLine().trim();
			while ((line != null) && (line.length() > 0) && (!line.equalsIgnoreCase("OK")))
			{
				i = line.indexOf(':');
				j = line.indexOf(',');
				memIndex = Integer.parseInt(line.substring(i + 1, j).trim());
				pdu = reader.readLine();
				if (isIncomingMessage(pdu))
				{
					messageList.add(new CIncomingMessage(pdu, memIndex));
					deviceInfo.getStatistics().incTotalIn();
				}
				else if (isStatusReportMessage(pdu))
				{
					messageList.add(new CStatusReportMessage(pdu, memIndex));
					deviceInfo.getStatistics().incTotalIn();
				}
				line = reader.readLine().trim();
			}
			reader.close();
		}
		else throw new NotConnectedException();
	}

	/**
		Send an SMS message from the GSM device. Once connected, you can create a
		COutgoingMessage object with the message you want to send, and pass it
		to this function.
		<br><br>
		<strong>Notes:</strong>
		<ul>
			<li>If you have set a phonebook, you can create the COutgoingMessage
				object with a nickname, instead of the actual phone number.</li>
		</ul>

		@param	message	a COutgoingMessage containing the message you wish to send.
		@return  true if sending succeded.
		@see	COutgoingMessage
		@see	CPhoneBook
		@see	CService#sendMessage(LinkedList)
		@see	CService#setPhoneBook(String)
	*/
	public boolean sendMessage(COutgoingMessage message) throws Exception
	{
		LinkedList messageList;
		COutgoingMessage msg;
		boolean result;

		messageList = new LinkedList();
		messageList.add(message);
		sendMessage(messageList);
		return (message.getDispatchDate() != null);
	}


	/**
		Send an series of SMS messages from the GSM device. This method is used
		when you want to send more than one message as a batch. If your GSM device
		support the feature of keeping the GSM link open during message dispatch,
		this method should work faster than calling the sendMessage(COutgoingMessage)
		method many times.
		<br>
		Just create a LinkedList object, add as many COutgoingMessage objects you wish
		and call the method.
		<br><br>
		<strong>Notes:</strong>
		<ul>
			<li>If you have set a phonebook, you can create the COutgoingMessage
				object with a nickname, instead of the actual phone number.</li>
		</ul>

		@param	messageList	a LinkedList filled with COutgoingMessage objects.
		@throws NotConnectedException
		@see	COutgoingMessage
		@see	CPhoneBook
		@see	CService#sendMessage(COutgoingMessage)
		@see	CService#setPhoneBook(String)
	*/
	public void sendMessage(LinkedList messageList) throws Exception
	{
		LinkedList outList;
		COutgoingMessage message;
		int i, j;
		String pdu;

		if (getConnected())
		{
			if (phoneBook.isLoaded()) outList = phoneBook.expandPhoneBookEntries(messageList);
			else outList = messageList;
			atHandler.keepGsmLinkOpen();
			for (i = 0; i < outList.size(); i ++)
			{
				message = (COutgoingMessage) outList.get(i);
				pdu = message.getPDU(smscNumber);
				j = pdu.length();
				j /= 2;
				if (smscNumber == null) ;
				else if (smscNumber.length() == 0) j --;
				else
				{
					j -= ((smscNumber.length() - 1) / 2);
					j -= 2;
				}
				if (atHandler.sendMessage(j, pdu))
				{
					message.setDispatchDate(new Date());
					deviceInfo.getStatistics().incTotalOut();
				}
				else message.setDispatchDate(null);
			}
		}
		else throw new NotConnectedException();
	}

	/**
		Deletes an SMS message from the GSM device memory.
		<br><br>
		<strong>Notes:</strong>
		<ul>
			<li>A deleted message cannot be recovered.</li>
			<li>It is highly recommended to use the other form of the deleteMessage()
				method.</li>
		</ul>

		@param	memIndex	the memory index of the GSM device's memory from where
				the message (if there is any message there) should be deleted.
		@return  True if delete operation succeded.
		@throws NotConnectedException
		@see	CService#deleteMessage(CIncomingMessage)
	*/
	public boolean deleteMessage(int memIndex) throws Exception
	{
		String response;

		if (getConnected())
		{
			if (memIndex > 0)	return atHandler.deleteMessage(memIndex);
			else return false;
		}
		else throw new NotConnectedException();
	}

	/**
		Deletes an SMS message from the GSM device memory.
		<br><br>
		<strong>Notes:</strong>
		<ul>
			<li>A deleted message cannot be recovered.</li>
		</ul>

		@param	message	a valid CIncomingMessage object, i.e. an object which is
				previously read with readMessages() from the GSM device.
		@return  True if delete operation succeded.
		@throws NotConnectedException
		@see	CIncomingMessage
		@see	CService#deleteMessage(int)
	*/
	public boolean deleteMessage(CIncomingMessage message) throws Exception
	{
		return deleteMessage(message.getMemIndex());
	}

	/**
		Sets the preferred message storage of the GSM device to SIM memory.

		@return  true if the change of preferred message storage succeded.
	*/
	public boolean setStorageSIM() throws Exception
	{
		return atHandler.setStorageSIM();
	}

	/**
		Sets the preferred message storage of the GSM device to build-in memory.

		@return  true if the change of preferred message storage succeded.
	*/
	public boolean setStorageMEM() throws Exception
	{
		return atHandler.setStorageMEM();
	}

	/**
		Refreshes the GSM device specific information. This method is called once during
		connection. Its up to the developer to call it periodically in order to get the latest
		information.

		Note: Information about Manufacturer, Model, SerialNo, IMSI, S/W Version are
		refreshed only once, since they don't change during a session.

		@see	CDeviceInfo
		@see	CService#connect()
		@see	CService#getDeviceInfo()
	*/
	public void refreshDeviceInfo() throws Exception
	{
		if (getConnected())
		{
			if (deviceInfo.manufacturer.length() == 0) deviceInfo.manufacturer = getManufacturer();
			if (deviceInfo.model.length() == 0) deviceInfo.model = getModel();
			if (deviceInfo.serialNo.length() == 0) deviceInfo.serialNo = getSerialNo();
			if (deviceInfo.imsi.length() == 0) deviceInfo.imsi = getImsi();
			if (deviceInfo.swVersion.length() == 0) deviceInfo.swVersion = getSwVersion();
			deviceInfo.batteryLevel = getBatteryLevel();
			deviceInfo.signalLevel = getSignalLevel();
		}
		else throw new NotConnectedException();
	}

	private String getManufacturer() throws Exception
	{
		String response;
		StringTokenizer tokens;

		String whatToDiscard;

		response = atHandler.getManufacturer();
		if (response.indexOf("OK") > -1)
		{
			tokens = new StringTokenizer(response, "\r");
			return tokens.nextToken();
		}
		else return VALUE_NOT_REPORTED;
	}

	private String getModel() throws Exception
	{
		String response;
		StringTokenizer tokens;

		String whatToDiscard;

		response = atHandler.getModel();
		if (response.indexOf("OK") > -1)
		{
			tokens = new StringTokenizer(response, "\r");
			return tokens.nextToken();
		}
		else return VALUE_NOT_REPORTED;
	}

	private String getSerialNo() throws Exception
	{
		String response;
		StringTokenizer tokens;

		String whatToDiscard;

		response = atHandler.getSerialNo();
		if (response.indexOf("OK") > -1)
		{
			tokens = new StringTokenizer(response, "\r");
			return tokens.nextToken();
		}
		else return VALUE_NOT_REPORTED;
	}

	private String getImsi() throws Exception
	{
		String response;
		StringTokenizer tokens;

		String whatToDiscard;

		response = atHandler.getImsi();
		if (response.indexOf("OK") > -1)
		{
			tokens = new StringTokenizer(response, "\r");
			return tokens.nextToken();
		}
		else return VALUE_NOT_REPORTED;
	}

	private String getSwVersion() throws Exception
	{
		String response;
		StringTokenizer tokens;

		String whatToDiscard;

		response = atHandler.getSwVersion();
		if (response.indexOf("OK") > -1)
		{
			tokens = new StringTokenizer(response, "\r");
			return tokens.nextToken();
		}
		else return VALUE_NOT_REPORTED;
	}

	private int getBatteryLevel() throws Exception
	{
		String response;
		StringTokenizer tokens;

		String whatToDiscard;

		response = atHandler.getBatteryLevel();
		if (response.indexOf("OK") > -1)
		{
			tokens = new StringTokenizer(response, "\r:,");
			tokens.nextToken(); tokens.nextToken();
			return Integer.parseInt(tokens.nextToken());
		}
		else return 0;
	}

	private int getSignalLevel() throws Exception
	{
		String response;
		StringTokenizer tokens;

		String whatToDiscard;

		response = atHandler.getSignalLevel();
		if (response.indexOf("OK") > -1)
		{
			tokens = new StringTokenizer(response, "\r:,");
			tokens.nextToken();
			return (Integer.parseInt(tokens.nextToken().trim()) * 100 / 31);
		}
		else return 0;
	}

	/**
		Checks if the message is SMS-DELIVER.

		@param pdu the message pdu
		@return true if the message is SMS-DELIVER
	*/
	private boolean isIncomingMessage(String pdu)
	{
		int index, i;

		i = Integer.parseInt(pdu.substring(0, 2), 16);
		index = (i + 1) * 2;

		i = Integer.parseInt(pdu.substring(index, index + 2), 16);
		if ((i & 0x03) == 0) return true;
		else return false;
	}

	/**
		Checks if the message is SMS-STATUS-REPORT.

		@param pdu the message pdu
		@return true if the message is SMS-STATUS-REPORT
	*/
	private boolean isStatusReportMessage(String pdu)
	{
		String str;
		int index, i;

		str = pdu.substring(0, 2);
		i = Integer.parseInt(str, 16);
		index = (i + 1) * 2;

		str = pdu.substring(index, index + 2);
		i = Integer.parseInt(str, 16);
		if ((i & 0x02) == 2) return true;
		else return false;
	}

	/**
		Virtual method, called upon receipt of a message (Asynchronous mode only!)
		<br><br>
		<strong>Notes:</strong>
		<ul>
			<li>If you plan to use jSMSEngine API in asynchronous mode, you should
				override this method, making it do your job upon message receipt.</li>
		</ul>

		@param	message the received message.
		@return  return true if you wish the message to be deleted from the GSM device's memory.
					Otherwise false.
		@see	CService#setReceiveMode(int)
	*/
	public @Deprecated boolean received(CIncomingMessage message)
	{
		return false;
	}

	private class CReceiveThread extends Thread
	{
		private boolean stopFlag;
		private boolean stopped;

		public CReceiveThread()
		{
			stopFlag = false;
			stopped = false;
		}

		public void killMe() { stopFlag = true; }
		public boolean killed() { return stopped; }

		public void run()
		{
			LinkedList messageList;

			messageList = new LinkedList();
			while (!stopFlag)
			{
				try { sleep(5000); } catch (Exception e) {}
				if (stopFlag) break;
				if ((getConnected()) && (getReceiveMode() == RECEIVE_MODE_ASYNC))
				{
					messageList.clear();
					try
					{
						readMessages(messageList, CIncomingMessage.CLASS_ALL);
						for (int i = 0; i < messageList.size(); i ++)
						{
							CIncomingMessage message = (CIncomingMessage) messageList.get(i);
							if (getMessageHandler() == null)
							{
								if (received(message)) deleteMessage(message);
							}
							else
							{
								if (getMessageHandler() != null && getMessageHandler().received(CService.this, message)) deleteMessage(message);
							}

						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			stopped = true;
		}
	}

	public static void main(String[] args)
	{
		System.out.println("jSMSEngine API.");
		System.out.println("	An open source Java API to process SMS messages from your ");
		System.out.println("	 mobile phone or gsm modem.");
		System.out.println("	This software is distributed under the LGPL license.");
		System.out.println("");
		System.out.println("Copyright (C) 2002-2006, Thanasis Delenikas, Athens / GREECE.");
		System.out.println("Visit http://www.jsmsengine.org for latest information.");
		System.out.println("\n");
		System.out.println(_name + " v" + _version + " { " + _reldate + " }");
	}
}
