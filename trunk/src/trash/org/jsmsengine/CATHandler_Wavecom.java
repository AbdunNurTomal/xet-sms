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

import java.util.logging.*;

/**
	AT Hanlder for Wavecom GSM devices.
*/
public class CATHandler_Wavecom extends CATHandler
{
	public CATHandler_Wavecom(CSerialDriver serialDriver, Logger log)
	{
		super(serialDriver, log);
	}

	public void reset() throws Exception
	{
		serialDriver.send("AT+CFUN=1\r");
		try { Thread.sleep(10000); } catch (Exception e) {}
		serialDriver.getResponse();
	}
}
