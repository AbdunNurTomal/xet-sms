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

import java.io.*;
import java.util.*;
import java.text.*;

/**
	This class has some general purpose functions.
*/
public class CUtils
{
	/**
		String substitution routine.

		@param	text	the initial text.
		@param	symbol	the string to be substituted.
		@param	value	the string that the "symbol" will be substituted with, in the "text" (all occurences).

		@return	the changed text.
	*/
	public static String substituteSymbol(String text, String symbol, String value)
	{
		StringBuffer buffer;

		while (text.indexOf(symbol) >= 0)
		{
			buffer = new StringBuffer(text);
			buffer.replace(text.indexOf(symbol), text.indexOf(symbol) + symbol.length(), value);
			text = buffer.toString();
		}
		return text;
	}
}
