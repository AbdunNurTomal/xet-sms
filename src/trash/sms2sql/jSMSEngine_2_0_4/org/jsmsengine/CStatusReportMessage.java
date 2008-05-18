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

import java.util.*;

/**
	This class represents a Status Report SMS message.

	In order to request a Status Report, use the setStatusReport() function of
		the outgoing message. Then, expect to eventually receive an IncomingMessage
		message of class CStatusReportMessage() (i.e. with type == TYPE_STATUS_REPORT).<br><br>
		Valid fields are:<br>
			recipient: the recipient of the original message.<br>
			text: a message reading the status of the delivery.<br>

	@see	CMessage
	@see	COutgoingMessage
	@see	COutgoingMessage#setStatusReport(boolean)
	@see	CIncomingMessage
	@see	CService#readMessages(LinkedList, int)
*/
public class CStatusReportMessage extends CIncomingMessage
{
	protected CStatusReportMessage(String pdu, int memIndex)
	{
		super(TYPE_STATUS_REPORT, memIndex);

		String recipient, text;
		int index, i, j, k, year, month, day, hour, min, sec;

		i = Integer.parseInt(pdu.substring(0, 2), 16);
		index = (i + 1) * 2;
		index += 4;

		i = Integer.parseInt(pdu.substring(index, index + 2), 16);
		j = index + 4;
		recipient = "";
		for (k = 0; k < i; k += 2) recipient = recipient + pdu.charAt(j + k + 1) + pdu.charAt(j + k);
		//recipient = "+" + recipient;
		if (recipient.charAt(recipient.length() - 1) == 'F') recipient = recipient.substring(0, recipient.length() - 1);

		index = j + i;
		index += 14;
		index += 14;
		i = Integer.parseInt(pdu.substring(index, index+1), 16);
		if ((i & 0x60) == 0) this.text = "00 - Succesful Delivery.";
		if ((i & 0x20) == 0x20) this.text = "01 - Errors, will retry dispatch.";
		if ((i & 0x40) == 0x40) this.text = "02 - Errors, stopped retrying dispatch.";
		if ((i & 0x60) == 0x60) this.text = "03 - Errors, stopped retrying dispatch.";

		this.recipient = recipient;
		this.date = null;
	}

	/**
		Returns the Originator of the message.
		Overloaded to return a valid value for CStatusReportMessage class.

		@return  the recipient of the original message.
	*/
	public String getOriginator() { return "TO:" + recipient; }
}
