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

/**
	This class contains the conversion routines to and from the standard 7bit
	GSM alphabet.
	<br><br>
	Every normal ASCII character must be converted according to the GSM 7bit
	default alphabet before dispatching through the GSM device. The opposite
	conversion is made when a message is received.
	<br><br>
	Since some characters in 7bit alphabet are in the position where control
	characters exist in the ASCII alphabet, each message is represented in
	HEX format as well (field hexText in CMessage class and descendants).
	When talking to the GSM device, either for reading messages, or for
	sending messages, a special mode is used where each character of the
	actual message is represented by two hexadecimal digits.
	So there is another conversion step here, in order to get the ASCII
	character from each pair of hex digits, and vice verca.
	<br><br>
	Note: currently, only GSM default 7Bit character set is supported.
	In all routines, you may assume the "charSet" parameter as constant.
*/
class CGSMAlphabets
{
	protected static final int GSM7BITDEFAULT = 1;

	private static final String alphabet = "@£$\u00A5\u00E8\u00E9\u00F9\u00EC\u00F2\u00C7\n\u00D8\u00F8\r\u00C5\u00E5ƒ_÷√ÀŸ–ÿ”»Œ@\u00C6\u00E6\u00DF\u00C9 !\"#\u00A4%&\'()*+,-./0123456789:;<=>?\u00A1ABCDEFGHIJKLMNOPQRSTUVWXYZ\u00C4\u00D6\u00D1\u00DCß\u00BFabcdefghijklmnopqrstuvwxyz\u00E4\u00F6\u00F1\u00FC\u00E0";

	/**
		Converts an ASCII character to its hexadecimal pair.

		@param	c	the ASCII character.
		@param	charSet	the target character set for the conversion.
		@return	the two hex digits which represent the character in the
				specific character set.
	*/
	protected static String char2Hex(char c, int charSet)
	{
		switch (charSet)
		{
			case GSM7BITDEFAULT:
				for (int i = 0; i < alphabet.length(); i ++)
					if (alphabet.charAt(i) == c) return (i <= 15 ? "0" + Integer.toHexString(i) : Integer.toHexString(i)); 
				break;
		}
		return (Integer.toHexString((int) c).length() < 2) ? "0" + Integer.toHexString((int) c) : Integer.toHexString((int) c);
	}

	/**
		Converts a hexadecimal value to the ASCII character it represents.

		@param	index	 the hexadecimal value.
		@param	charSet	the character set in which "index" is represented.
		@return  the ASCII character which is represented by the hexadecimal value.
	*/
	protected static char hex2Char(int index, int charSet)
	{
		switch (charSet)
		{
			case GSM7BITDEFAULT:
				if (index < alphabet.length()) return alphabet.charAt(index);
				else return '?';
		}
		return '?';			
	}

	/**
		Converts a int value to the extended ASCII character it represents.
		@param	ch	 the int value.
		@param	charSet	the character set in which "ch" is represented.
		@return  the extended ASCII character which is represented by the int value.
	*/
	protected static char hex2ExtChar(int ch, int charSet)
	{
		switch (charSet)
		{
			case GSM7BITDEFAULT:
				switch (ch)
				{
					case 10:
						return '\f';
					case 20:
						return '^';
					case 40:
						return '{';
					case 41:
						return '}';
					case 47:
						return '\\';
					case 60:
						return '[';
					case 61:
						return '~';
					case 62:
						return ']';
					case 64:
						return '|';
					case 101:
						return '\u20AC';
					default:
						return '?';
				}
			default:
				return '?';
		}
	}

	/**
		Converts the given ASCII string to a string of hexadecimal pairs.

		@param	text	the ASCII string.
		@param	charSet	the target character set for the conversion.
		@return	the string of hexadecimals pairs which represent the "text"
				parameter in the specified "charSet".
	*/
	protected static String text2Hex(String text, int charSet)
	{
		String outText = "";

		for (int i = 0; i < text.length(); i ++)
		{
			switch (text.charAt(i))
			{
				case '¡': case '·': case '‹':
					outText = outText + char2Hex('A', charSet);
					break;
				case '¬': case '‚':
					outText = outText + char2Hex('B', charSet);
					break;
				case '√': case '„':
					outText = outText + char2Hex('√', charSet);
					break;
				case 'ƒ': case '‰':
					outText = outText + char2Hex('ƒ', charSet);
					break;
				case '≈': case 'Â': case '›':
					outText = outText + char2Hex('E', charSet);
					break;
				case '∆': case 'Ê':
					outText = outText + char2Hex('Z', charSet);
					break;
				case '«': case 'Á': case 'ﬁ':
					outText = outText + char2Hex('H', charSet);
					break;
				case '»': case 'Ë':
					outText = outText + char2Hex('»', charSet);
					break;
				case '…': case 'È': case 'ﬂ':
					outText = outText + char2Hex('I', charSet);
					break;
				case ' ': case 'Í':
					outText = outText + char2Hex('K', charSet);
					break;
				case 'À': case 'Î':
					outText = outText + char2Hex('À', charSet);
					break;
				case 'Ã': case 'Ï':
					outText = outText + char2Hex('M', charSet);
					break;
				case 'Õ': case 'Ì':
					outText = outText + char2Hex('N', charSet);
					break;
				case 'Œ': case 'Ó':
					outText = outText + char2Hex('Œ', charSet);
					break;
				case 'œ': case 'Ô': case '¸':
					outText = outText + char2Hex('O', charSet);
					break;
				case '–': case '':
					outText = outText + char2Hex('–', charSet);
					break;
				case '—': case 'Ò':
					outText = outText + char2Hex('P', charSet);
					break;
				case '”': case 'Û': case 'Ú':
					outText = outText + char2Hex('”', charSet);
					break;
				case '‘': case 'Ù':
					outText = outText + char2Hex('T', charSet);
					break;
				case '’': case 'ı': case '˝':
					outText = outText + char2Hex('Y', charSet);
					break;
				case '÷': case 'ˆ':
					outText = outText + char2Hex('÷', charSet);
					break;
				case '◊': case '˜':
					outText = outText + char2Hex('X', charSet);
					break;
				case 'ÿ': case '¯':
					outText = outText + char2Hex('ÿ', charSet);
					break;
				case 'Ÿ': case '˘': case '˛':
					outText = outText + char2Hex('Ÿ', charSet);
					break;
				case '\f':
					outText = outText + Integer.toHexString(27) + Integer.toHexString(10);
					break;
				case '^':
					outText = outText + Integer.toHexString(27) + Integer.toHexString(20);
					break;
				case '{':
					outText = outText + Integer.toHexString(27) + Integer.toHexString(40);
					break;
				case '}':
					outText = outText + Integer.toHexString(27) + Integer.toHexString(41);
					break;
				case '\\':
					outText = outText + Integer.toHexString(27) + Integer.toHexString(47);
					break;
				case '[':
					outText = outText + Integer.toHexString(27) + Integer.toHexString(60);
					break;
				case '~':
					outText = outText + Integer.toHexString(27) + Integer.toHexString(61);
					break;
				case ']':
					outText = outText + Integer.toHexString(27) + Integer.toHexString(62);
					break;
				case '|':
					outText = outText + Integer.toHexString(27) + Integer.toHexString(64);
					break;
				case '\u20AC':
					outText = outText + Integer.toHexString(27) + Integer.toHexString(101);
					break;
				default:
					outText = outText + char2Hex(text.charAt(i), charSet);
					break;
			}
		}
		return outText;
	}

	/**
		Converts the given string of hexadecimal pairs to its ASCII equivalent string.

		@param	text	the hexadecimal pair string.
		@param	charSet	the target character set for the conversion.
		@return	the ASCII string.
	*/
	protected static String hex2Text(String text, int charSet)
	{
		String outText = "";

		for (int i = 0; i < text.length(); i += 2)
		{
			String hexChar = "" + text.charAt(i) + text.charAt(i + 1);
			int c = Integer.parseInt(hexChar, 16);
			if (c == 27)
			{
				i ++;
				outText = outText + hex2ExtChar((char) c, charSet);
			}
			else outText = outText + hex2Char((char) c, charSet);
		}
		return outText;
	}
}
