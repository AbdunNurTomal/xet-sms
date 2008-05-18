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
import gnu.io.*;

/**
	This class handles the operation the serial port.
	<br><br>
	This class contains all the necessary (low-level) functions that handle COMM API
	and are responsible for the serial communication with the GSM device.
	<br><br>
	Comments left to be added in next release.
*/
class CSerialDriver  implements SerialPortEventListener
{
	/**
		Timeout period for the phone to respond to jSMSEngine.
	*/
	private static final int RECV_TIMEOUT = 30 * 1000;

	/**
		Input/Output buffer size for serial communication.
	*/
	private static final int BUFFER_SIZE = 16000;

	private String port;
	private int baud;

	private CommPortIdentifier portId;
	private SerialPort serialPort;
	private InputStream inStream;
	private OutputStream outStream;

	private Logger log;

	public CSerialDriver(String port, int baud, Logger log)
	{
		this.port = port;
		this.baud = baud;
		this.log = log;
	}

	public void setPort(String port) { this.port = port; }
	public String getPort() { return port; }
	public int getBaud() { return baud; }

	public void open() throws Exception
	{
		Enumeration portList;

		log.log(Level.INFO, "Connecting...");

		portId = CommPortIdentifier.getPortIdentifier(getPort());
		serialPort = (SerialPort) portId.open("jSMSEngine", 1971);
		inStream = serialPort.getInputStream();
		outStream = serialPort.getOutputStream();
		serialPort.notifyOnDataAvailable(true);
		serialPort.notifyOnOutputEmpty(true);
		serialPort.notifyOnBreakInterrupt(true);
		serialPort.notifyOnFramingError(true);
		serialPort.notifyOnOverrunError(true);
		serialPort.notifyOnParityError(true);
		//serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
		serialPort.addEventListener(this);
		serialPort.setSerialPortParams(getBaud(), SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		serialPort.setInputBufferSize(BUFFER_SIZE);
		serialPort.setOutputBufferSize(BUFFER_SIZE);
		serialPort.enableReceiveTimeout(RECV_TIMEOUT);
	}

	public void close()
	{
		log.log(Level.INFO, "Disconnecting...");
		try { serialPort.close(); } catch (Exception e) {}
	}

	public void serialEvent(SerialPortEvent event)
	{
		switch(event.getEventType())
		{
			case SerialPortEvent.BI:
				break;
			case SerialPortEvent.OE:
				log.log(Level.SEVERE, "COMM-ERROR: Overrun Error!");
				break;
			case SerialPortEvent.FE:
				log.log(Level.SEVERE, "COMM-ERROR: Framing Error!");
				break;
			case SerialPortEvent.PE:
				log.log(Level.SEVERE, "COMM-ERROR: Parity Error!");
				break;
			case SerialPortEvent.CD:
				break;
			case SerialPortEvent.CTS:
				break;
			case SerialPortEvent.DSR:
				break;
			case SerialPortEvent.RI:
				break;
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				break;
			case SerialPortEvent.DATA_AVAILABLE:
				break;
		}
	}
	public void clearBuffer() throws Exception
	{
		while (dataAvailable()) inStream.read();
	}

	public void send(String s) throws Exception
	{
		log.log(Level.INFO, "TE: " + formatLog(new StringBuffer(s)));
		for (int i = 0; i < s.length(); i ++)
		{
			outStream.write((byte) s.charAt(i));
		}
		outStream.flush();
	}

	public void send(char c) throws Exception
	{
		outStream.write((byte) c);
		outStream.flush();
	}

	public void skipBytes(int numOfBytes) throws Exception
	{
		int count, c;

		count = 0;
		while (count < numOfBytes)
		{
			c = inStream.read();
			if (c != -1) count ++;
		}
	}

	public boolean dataAvailable() throws Exception
	{
		return (inStream.available() > 0 ? true : false);
	}

	public String getResponse() throws Exception
	{
		final int RETRIES = 3;
		final int WAIT_TO_RETRY = 1000;
		StringBuffer buffer;
		int c, retry;

		retry = 0;
		buffer = new StringBuffer(BUFFER_SIZE);

		while (retry < RETRIES)
		{
			try
				{
					while (true)
					{
						c = inStream.read();
						if (c == -1)
						{
							buffer.delete(0, buffer.length());
							break;
						}
						buffer.append((char) c);
						if  ((buffer.toString().indexOf("\r\nOK\r") > -1) || (buffer.toString().indexOf("\r\nERROR\r") > -1)) break;
					}
					retry = RETRIES;
				}
			catch (Exception e)
			{
				if (retry < RETRIES)
				{
					Thread.sleep(WAIT_TO_RETRY);
					retry ++;
				}
				else throw e;
			}
		}
		log.log(Level.INFO, "ME: " + formatLog(buffer));
		if (dataAvailable()) skipBytes(1);
		while ((buffer.length() > 1) && ((buffer.charAt(0) == '\r') || (buffer.charAt(0) == '\n'))) buffer.delete(0, 1);
		return buffer.toString();
	}

	private String formatLog(StringBuffer s)
	{
		String response;
		int i;

		response = "";
		for (i = 0; i < s.length(); i ++)
		{
			switch (s.charAt(i))
			{
				case 13 :
					response += "<cr>";
					break;
				case 10 :
					response += "<lf>";
					break;
				case 9 :
					response += "<tab>";
					break;
				default:
					response += "<" + (int) s.charAt(i) + ">";
					break;
			}
		}
		response += "  Text:[";
		for (i = 0; i < s.length(); i ++)
		{
			switch (s.charAt(i))
			{
				case 13 :
					response += "<cr>";
					break;
				case 10 :
					response += "<lf>";
					break;
				case 9 :
					response += "<tab>";
					break;
				default:
					response += s.charAt(i);
					break;
			}
		}
		response += "]";
		return response;
	}
}
