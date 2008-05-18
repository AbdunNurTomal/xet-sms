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

import java.util.logging.*;

/**
	This class contains all the AT commands which are used during communication
	of the API with the GSM device.<br><br>
	This is the generic handler. It should be compatible with all devices. However, "should" is
	a big word... Some GSM modems may need slightly different AT commands to work. In this
	case, subclassing of CATHandler() is required.<br><br>
	<strong>Note for those creating subclassed versions of CATHandler for their own modems:</strong><br>
	All methods that are marked as "<strong>***Critical method***</strong>" should be implemented, 
	otherwise the jSMSEngine API will not work. The Non-Critical may be left unimplemented - they should 
	not affect the normal jSMSEngine API operation.
*/
public class CATHandler
{
	protected CSerialDriver serialDriver;
	protected Logger log;

	public CATHandler(CSerialDriver serialDriver, Logger log)
	{
		this.serialDriver = serialDriver;
		this.log = log;
	}

	/**
		Sends a couple of "AT" commands in order to sync with modem.<br>
		Useful with auto-baud-detecting GSM devices.<br>
		<strong>***Critical method***</strong>
	*/
	public void sync() throws Exception
	{
		serialDriver.send("AT\r");
		serialDriver.getResponse();
		serialDriver.send("AT\r");
		serialDriver.getResponse();
	}

	/**
		This command should soft-reset the modem.<br>
		Soft-reset is not available for all GSM devices, and the operation is not 
		critical.<br>
		<strong>***Non-Critical method***</strong>
	*/
	public void reset() throws Exception
	{
		serialDriver.send("AT+CFUN=1\r");
		try { Thread.sleep(10000); } catch (Exception e) {}
		serialDriver.getResponse();
	}

	/**
		Sets Echo off.<br>
		<strong>***Critical method***</strong>
	*/
	public void echoOff() throws Exception
	{
		serialDriver.send("ATE0\r");
		serialDriver.getResponse();
	}

	/**
		Other initialization commands.<br>
		This is left empty, but you might need to add specific code for your specific modem.<br>
		<strong>***Non-Critical method***</strong>
	*/
	public void init() throws Exception
	{
	}

	/**
		Sends an "AT" command and waits for response.<br>
		<strong>***Critical method***</strong>

		@return  True if GSM device responded with "OK".
	*/
	public boolean isAlive() throws Exception
	{
		serialDriver.send("AT\r");
		return (serialDriver.getResponse().indexOf("OK\r") >= 0);
	}

	/**
		Requests status regarding PIN.<br>
		<strong>***Critical method***</strong>

		@return  True if GSM device is waiting for PIN to be entered.
	*/
	public boolean waitingForPin() throws Exception
	{
		serialDriver.send("AT+CPIN?\r");
		return (serialDriver.getResponse().indexOf("SIM PIN") >= 0);
	}

	/**
		Enters PIN.<br>
		<strong>***Critical method***</strong>

		@return  True if GSM device unlocked. False if PIN is wrong.
	*/
	public boolean enterPin(String pin) throws Exception
	{
		serialDriver.send(CUtils.substituteSymbol("AT+CPIN=\"{1}\"\r", "{1}", pin));
		Thread.sleep(5000);
		if (serialDriver.getResponse().indexOf("OK\r") >= 0)
		{
			try { Thread.sleep(10000); } catch (Exception e) {} 
			return true;
		}
		else return false;
	}

	/**
		Sets verbose mode for error reporting.<br>
		<strong>***Non-Critical method***</strong>

		@return  True if GSM device accepted the option.
	*/
	public boolean setVerboseErrors() throws Exception
	{
		serialDriver.send("AT+CMEE=1\r");
		return (serialDriver.getResponse().equalsIgnoreCase("OK\r"));
	}

	/**
		Sets PDU operation.<br>
		<strong>***Critical method***</strong>

		@return  True if GSM device supports PDU operation.
	*/
	public boolean setPduMode() throws Exception
	{
		serialDriver.send("AT+CMGF=0\r");
		return (serialDriver.getResponse().equalsIgnoreCase("OK\r"));
	}

	/**
		Disable GSM device originated delivery notifications.<br>
		<strong>***Critical method***</strong>

		@return  True if GSM device disabled notifications.
	*/
	public boolean  disableIndications() throws Exception
	{
		serialDriver.send("AT+CNMI=0,0,0,0\r");
		return (serialDriver.getResponse().equalsIgnoreCase("OK\r"));
	}

	/**
		Returns the Manufacturer string.<br>
		<strong>***Critical method***</strong>

		@return  The Manufacturer string.
	*/
	public String getManufacturer() throws Exception
	{
		serialDriver.send("AT+CGMI\r");
		return serialDriver.getResponse();
	}

	/**
		Returns the Model string.<br>
		<strong>***Critical method***</strong>

		@return  The Model string.
	*/
	public String getModel() throws Exception
	{
		serialDriver.send("AT+CGMM\r");
		return serialDriver.getResponse();
	}

	/**
		Returns the GSM Device serial number.<br>
		<strong>***Critical method***</strong>

		@return  The GSM Device serial number.
	*/
	public String getSerialNo() throws Exception
	{
		serialDriver.send("AT+CGSN\r");
		return serialDriver.getResponse();
	}

	/**
		Returns the IMSI (International Mobile Subscriber Identity).<br>
		<strong>***Critical method***</strong>

		@return  The IMSI String.
	*/
	public String getImsi() throws Exception
	{
		serialDriver.send("AT+CIMI\r");
		return serialDriver.getResponse();
	}

	/**
		Returns the GSM device software version.<br>
		<strong>***Critical method***</strong>

		@return  The software version.
	*/
	public String getSwVersion() throws Exception
	{
		serialDriver.send("AT+CGMR\r");
		return serialDriver.getResponse();
	}

	/**
		Returns the battery level.<br>
		<strong>***Critical method***</strong>

		@return  The battery level.
	*/
	public String getBatteryLevel() throws Exception
	{
		serialDriver.send("AT+CBC\r");
		return serialDriver.getResponse();
	}

	/**
		Returns the signal level.<br>
		<strong>***Critical method***</strong>

		@return  The signal level.
	*/
	public String getSignalLevel() throws Exception
	{
		serialDriver.send("AT+CSQ\r");
		return serialDriver.getResponse();
	}

	/**
		Sets the preferred message storage to Memory.<br>
		<strong>***Non-Critical method***</strong>

		@return  True if operated succeded.
	*/
	public boolean setStorageMEM() throws Exception
	{
		serialDriver.send("AT+CPMS=\"ME\"\r");
		return (serialDriver.getResponse().equalsIgnoreCase("OK\r"));
	}

	/**
		Sets the preferred message storage to SIM.<br>
		<strong>***Non-Critical method***</strong>

		@return  True if operated succeded.
	*/
	public boolean setStorageSIM() throws Exception
	{
		serialDriver.send("AT+CPMS=\"SM\"\r");
		return (serialDriver.getResponse().equalsIgnoreCase("OK\r"));
	}

	/**
		Switches GSM device to command mode.<br>
		<strong>***Critical method***</strong>
	*/
	public void switchToCmdMode() throws Exception
	{
		serialDriver.send("+++");
		serialDriver.clearBuffer();
	}

	/**
		Keeps the GSM link open for sending multiple messages in less time.<br>
		Not critical. If this option is supported, message dispatch may be a little 
		faster.<br>
		<strong>***Non-Critical method***</strong>

		@return  True if operated succeded.
	*/
	public boolean keepGsmLinkOpen() throws Exception
	{
		serialDriver.send("AT+CMMS=1\r");
		return (serialDriver.getResponse().equalsIgnoreCase("OK\r"));
	}

	/**
		Sends a message.<br>
		In case of errors, it rerties up to three times to complete the operation 
		before it returns failure.<br>
		<strong>***Critical method***</strong>

		@return  True if operated succeded.
	*/
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
				log.log(Level.SEVERE, "ATHandler().SendMessage(): Still waiting for response (I) (" + responseRetries + ")...");
				Thread.sleep(5000);
			}
			responseRetries = 0;
			serialDriver.clearBuffer();
			serialDriver.send(pdu);
			serialDriver.send((char) 26);
			response = serialDriver.getResponse();
			while (response.length() == 0)
			{
				responseRetries ++;
				if (responseRetries == 4)  throw new NoResponseException();
				log.log(Level.SEVERE, "ATHandler().SendMessage(): Still waiting for response (II) (" + responseRetries + ")...");
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

	/**
		Returns the list of messages.<br>
		<strong>***Critical method***</strong>

		@return  True if operated succeded.
	*/
	public String listMessages(int messageClass) throws Exception
	{
		String command;

		switch (messageClass)
		{
			case CIncomingMessage.CLASS_ALL:
				serialDriver.send("AT+CMGL=4\r");
				break;
			case CIncomingMessage.CLASS_REC_UNREAD:
				serialDriver.send("AT+CMGL=0\r");
				break;
			case CIncomingMessage.CLASS_REC_READ:
				serialDriver.send("AT+CMGL=1\r");
				break;
			case CIncomingMessage.CLASS_STO_UNSENT:
				serialDriver.send("AT+CMGL=2\r");
				break;
			case CIncomingMessage.CLASS_STO_SENT:
				serialDriver.send("AT+CMGL=3\r");
				break;
		}
		return serialDriver.getResponse();
	}

	/**
		Deletes a message from GSM device memory.<br>
		<strong>***Critical method***</strong>

		@return  True if operated succeded.
	*/
	public boolean deleteMessage(int memIndex) throws Exception
	{
		serialDriver.send(CUtils.substituteSymbol("AT+CMGD={1}\r", "{1}", "" + memIndex));
		return (serialDriver.getResponse().equalsIgnoreCase("OK\r"));
	}
}
