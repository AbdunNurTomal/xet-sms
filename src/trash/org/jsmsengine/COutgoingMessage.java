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

package org.jsmsengine;

import java.util.*;

/**
	This class represents an outgoing SMS message, i.e. message created for dispatch
	from the GSM device.

	@see	CMessage
	@see	CIncomingMessage
	@see	CPhoneBook
	@see	CService#sendMessage(COutgoingMessage)
	@see	CService#sendMessage(LinkedList)
*/
public class COutgoingMessage extends CMessage
{
	private Date dispatchDate;
	private int validityPeriod;
	private boolean statusReport;
	private boolean flashSms;

	private int srcPort;
	private int dstPort;

	/**
		Default constructor of the class.
	*/
	public COutgoingMessage()
	{
		super(TYPE_OUTGOING, null, null, null, null, -1);
		setDispatchDate(null);
		setDate(new Date());
		statusReport = false;

		srcPort = -1;
		dstPort = -1;
	}

	/**
		Constructor of the class.

		@param	recipient	the recipients's number.
		@param	text	the actual text of the message.

		<br><br>Notes:<br>
		<ul>
			<li>Phone numbers are represented in their international or national format.</li>
			<li>If you use a phonebook, the phone number may be a string starting with the '~' character,
					representing an entry in the phonebook.</li>
			<li>By default, a created message is set to be encoded in 7bit. If you want to change that, be sure
					to operate in PDU mode, and change the encoding with setMessageEncoding() method.</li>
		</ul>
	*/
	public COutgoingMessage(String recipient, String text)
	{
		super(TYPE_OUTGOING, new Date(), null, recipient, text, -1);
		setDispatchDate(null);
		setDate(new Date());

		validityPeriod = -1;
		statusReport = false;
		flashSms = false;
		srcPort = -1;
		dstPort = -1;
	}

	/**
		Set the phone number of the recipient. Applicable to outgoing messages.

		@param	recipient	the recipient's phone number (international format).
	*/
	public void setRecipient(String recipient) { this.recipient = recipient; }

	/**
		Sets the dispatch date of the message.

		@param	date	the dispatch date of the message.
	*/
	protected void setDispatchDate(Date date) { this.dispatchDate = date; }

	/**
		Sets the validity period.

		@param hours The hours of validity period. Validity is expressed in relative format, i.e. starts counting
			from the time the SMSC receives the message. A value of -1 will set the maximum validity period.
	*/
	public void setValidityPeriod(int hours) { this.validityPeriod = hours; }

	/**
		Sets if a status report is requested.

		@param statusReport True if a status report is requested. Default is false (no status report).
	*/
	public void setStatusReport(boolean statusReport) { this.statusReport = statusReport; }

	/**
		Sets the message to be delivered as a flash message.

		@param flashSms True if the message should be delivered as a flash SMS message.
	*/
	public void setFlashSms(boolean flashSms) { this.flashSms = flashSms; }

	/**
		Sets the source port. Set to -1 for no  port.

		@param port The source port (16bit addressing).
	*/
	public void setSourcePort(int port) { this.srcPort = port; }

	/**
		Sets the destination port. Set to -1 for no port.

		@param port The destination port (16bit addressing).
	*/
	public void setDestinationPort(int port) { this.dstPort = port; }

	/**
		Returns the recipient's phone number (international format). <br>
		<strong>This may be an entry from the phonebook.</strong>

		@return  the type of the message.
	*/
	public String getRecipient() { return recipient; }

	/**
		Returns the dispatch date of the message.

		@return  The dispatch date of the message.
	*/
	public Date getDispatchDate() { return dispatchDate; }

	/**
		Returns the validity period (hours).

		@return The validity period (hours).
	*/
	public int getValidityPeriod() { return validityPeriod; }

	/**
		Returns true if a status report is requested.

		@return True if a status report is requested.
	*/
	public boolean getStatusReport() { return statusReport; }

	/**
		Returns true if this is a flash message.

		@return True if this is a flash message.
	*/
	public boolean getFlashSms() { return flashSms; }

	/**
		Returns the source port. -1 means no port.

		@return The source port.
	*/
	public int getSourcePort() { return srcPort; }

	/**
		Returns the destination port. -1 means no port.

		@return The destination port.
	*/
	public int getDestinationPort() { return dstPort; }


	public String getPDU(String smscNumber)
	{
		String pdu, udh;
		String str1, str2, str3;
		int i, high, low;
		char c;

		pdu = "";
		udh = "";
		if ((smscNumber != null) && (smscNumber.length() != 0))
		{
			str1 = "91" + toBCDFormat(smscNumber.substring(1));
			str2 = Integer.toHexString(str1.length() / 2);
			if (str2.length() != 2) str2 = "0" + str2;
			pdu = pdu + str2 + str1;
		}
		else if ((smscNumber != null) && (smscNumber.length() == 0)) pdu = pdu + "00";
		if ((srcPort != -1) && (dstPort != -1))
		{
			if (statusReport) pdu = pdu + "71";
			else pdu = pdu + "51";
		}
		else
		{
			if (statusReport) pdu = pdu + "31";
			else pdu = pdu + "11";
		}
		pdu = pdu + "00";
		str1 = getRecipient();
		if( str1.charAt(0) == '+' )
		{
			str1 = toBCDFormat(str1.substring(1));
			str2 = Integer.toHexString(getRecipient().length() - 1);
			str1 = "91" + str1;
		}
		else
		{
			str1 = toBCDFormat(str1);
			str2 = Integer.toHexString(getRecipient().length());
			str1 = "81" + str1;
		}
		if (str2.length() != 2) str2 = "0" + str2;

		pdu = pdu + str2 + str1;
		pdu = pdu + "00";
		switch (getMessageEncoding())
		{
			case MESSAGE_ENCODING_7BIT:
				if (flashSms) pdu = pdu + "10";
				else pdu = pdu + "00";
				break;
			case MESSAGE_ENCODING_8BIT:
				if (flashSms) pdu = pdu + "14";
				else pdu = pdu + "04";
				break;
			case MESSAGE_ENCODING_UNICODE:
				if (flashSms) pdu = pdu + "18";
				else pdu = pdu + "08";
				break;
		}
		pdu = pdu + getValidityPeriodBits();
		if ((srcPort != -1) && (dstPort != -1))
		{
			String s;

			udh = "060504";
			s = Integer.toHexString(dstPort);
			while (s.length() < 4) s = "0" + s;
			udh += s;
			s = Integer.toHexString(srcPort);
			while (s.length() < 4) s = "0" + s;
			udh += s;
		}

		switch (getMessageEncoding())
		{
			case MESSAGE_ENCODING_7BIT:
				str2 = textToPDU(getText());
				if ((srcPort != -1) && (dstPort != -1)) str1 = Integer.toHexString(getText().length() + 8);
				else str1 = Integer.toHexString(getText().length());
				// ******************************************
				// Replaced with two lines above due to reported bug with incorrect message encoding.
				// Thanasis @05/02/2006
				//
				//if ((srcPort != -1) && (dstPort != -1)) str1 = Integer.toHexString((str2.length() * 4 / 7) + 8);
				//else str1 = Integer.toHexString(str2.length() * 4 / 7);
				// ******************************************
				if (str1.length() != 2) str1 = "0" + str1;
				if ((srcPort != -1) && (dstPort != -1)) pdu = pdu + str1 + udh + str2;
				else pdu = pdu + str1 + str2;
				break;
			case MESSAGE_ENCODING_8BIT:
				str1 = getText();
				str2 = "";
				for (i = 0; i < str1.length(); i ++)
				{
					c = str1.charAt(i);
					str2 = str2 + ((Integer.toHexString((int) c).length() < 2) ? "0" + Integer.toHexString((int) c) : Integer.toHexString((int) c));  
				}
				if ((srcPort != -1) && (dstPort != -1)) str1 = Integer.toHexString(getText().length() + 7);
				else str1 = Integer.toHexString(getText().length());
				if (str1.length() != 2) str1 = "0" + str1;
				if ((srcPort != -1) && (dstPort != -1)) pdu = pdu + str1 + udh + str2;
				else pdu = pdu + str1 + str2;
				break;
			case MESSAGE_ENCODING_UNICODE:
				str1 = getText();
				str2 = "";
				for (i = 0; i < str1.length(); i ++)
				{
					c = str1.charAt(i);
					high = (int) (c / 256);
					low = c % 256;
					str2 = str2 + ((Integer.toHexString(high).length() < 2) ? "0" + Integer.toHexString(high) : Integer.toHexString(high));
					str2 = str2 + ((Integer.toHexString(low).length() < 2) ? "0" + Integer.toHexString(low) : Integer.toHexString(low));
				}
				if ((srcPort != -1) && (dstPort != -1)) str1 = Integer.toHexString((getText().length() * 2) + 7);
				else str1 = Integer.toHexString(getText().length() * 2);
				if (str1.length() != 2) str1 = "0" + str1;
				if ((srcPort != -1) && (dstPort != -1)) pdu = pdu + str1 + udh + str2;
				else pdu = pdu + str1 + str2;
				break;
		}
		return pdu.toUpperCase();
	}

	private String getValidityPeriodBits()
	{
		String bits;
		int value;

		if (validityPeriod == -1) bits = "FF";
		else
		{
			if (validityPeriod <= 12) value = (validityPeriod * 12) - 1;
			else if (validityPeriod <= 24) value = (((validityPeriod - 12) * 2) + 143);
			else if (validityPeriod <= 720) value = (validityPeriod / 24) + 166;
			else value = (validityPeriod / 168) + 192;
			bits = Integer.toHexString(value);
			if (bits.length() != 2) bits = "0" + bits;
			if (bits.length() > 2) bits = "FF";
		}
		return bits;
	}

	private String textToPDU(String text)
	{
		String pdu, str1;
		byte[] oldBytes, newBytes;
		BitSet bitSet;
		int i, j, k, value1, value2;

		str1 = "";		
		text = CGSMAlphabets.text2Hex(text, CGSMAlphabets.GSM7BITDEFAULT);
		for (i = 0; i < text.length(); i += 2)
		{
			j = (Integer.parseInt("" + text.charAt(i), 16) * 16) + Integer.parseInt("" + text.charAt(i + 1), 16);
			str1 += (char) j;
		}
		text = str1; 
		oldBytes = text.getBytes();
		bitSet = new BitSet(text.length() * 8);

		value1 = 0;
		for (i = 0; i < text.length(); i ++)
			for (j = 0; j < 7; j ++)
			{
				value1 = (i * 7) + j;
				if ((oldBytes[i] & (1 << j)) != 0) bitSet.set(value1);
			}
		value1 ++;

		if (((value1 / 56) * 56) != value1) value2 = (value1 / 8) + 1;
		else value2 = (value1 / 8);
		if (value2 == 0) value2 = 1;

		newBytes = new byte[value2];
		for (i = 0; i < value2; i ++)
			for (j = 0; j < 8; j ++)
				if ((value1 + 1) > ((i * 8) + j))
					if (bitSet.get(i * 8 + j)) newBytes[i] |= (byte) (1 << j);

		pdu = "";
		for (i = 0; i < value2; i ++)
		{
			str1 = Integer.toHexString((int) newBytes[i]);
			if (str1.length() != 2) str1 = "0" + str1;
			str1 = str1.substring(str1.length() - 2, str1.length());
			pdu += str1;
		}
		return pdu;
	}

	private String toBCDFormat(String s)
	{
		String bcd;
		int i;

		if ((s.length() % 2) != 0) s = s + "F";
		bcd = "";
		for (i = 0; i < s.length(); i += 2) bcd = bcd + s.charAt(i + 1) + s.charAt(i);
		return bcd; 
	}
}
