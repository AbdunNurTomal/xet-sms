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

class CConstants
{
	public static final String MAIN_WINDOW_TITLE = "jSMSServer";

	public static final String ABOUT_VERSION = "<html><h1><b>jSMSServer v2.0.4</b></h1></html>";
	public static final String ABOUT_BASED = "<html>Based on jSMSEngine API</html>";
	public static final String ABOUT_BY = "<html><b>Copyright (C) 2002-2006, Thanasis Delenikas</b><br></html>";
	public static final String ABOUT_WEBPAGE = "<html><b>http://www.jsmsengine.org</b></html>";
	public static final String ABOUT_EMAIL = "";
	public static final String ABOUT_OTHER = "This software is distributed under the LPGL license.";

	public static final String MENU_FILE_MAIN = "Action";
	public static final String MENU_FILE_OPTION_01 = "Connect";
	public static final String MENU_FILE_OPTION_02 = "Disconnect";
	public static final String MENU_FILE_OPTION_99 = "Exit";
	public static final String MENU_ABOUT_MAIN = "About";
	public static final String MENU_ABOUT_OPTION_01 = "About jSMSServer...";

	public static final String LABEL_MANUFACTURER = "Manufacturer:";
	public static final String LABEL_MODEL = "Model:";
	public static final String LABEL_SERIALNO = "Serial No:";
	public static final String LABEL_IMSI = "IMSI:";
	public static final String LABEL_SWVERSION = "S/W Version:";
	public static final String LABEL_BATTERY = "Battery Status: ";
	public static final String LABEL_SIGNAL = "Signal Status: ";
	public static final String LABEL_STATUS = "Status: ";

	public static final String LABEL_INCOMING_FROM = "Originator: ";
	public static final String LABEL_INCOMING_DATE = "Date: ";
	public static final String LABEL_INCOMING_TEXT = "Text: ";
	public static final String LABEL_OUTGOING_TO = "Recipient: ";
	public static final String LABEL_OUTGOING_DATE = "Date: ";
	public static final String LABEL_OUTGOING_TEXT = "Text: ";
	public static final String LABEL_UP_SINCE = "On-Line since: ";
	public static final String LABEL_TRAFFIC = "Total SMS: ";
	public static final String LABEL_TRAFFIC_IN = "In: ";
	public static final String LABEL_TRAFFIC_OUT = "Out: ";
	public static final String LABEL_INTERFACES = "Interfaces: ";
	public static final String LABEL_INTERFACE_DB_OFF = "<html><font color='#b0b0b0'><strong>JDBC</strong></font></html>";
	public static final String LABEL_INTERFACE_DB_ON = "<html><font color='#00b000'><strong>JDBC</strong></font></html>";
	public static final String LABEL_INTERFACE_XML_OFF = "<html><font color='#b0b0b0'><strong>XML</strong></font></html>";
	public static final String LABEL_INTERFACE_XML_ON = "<html><font color='#00b000'><strong>XML</strong></font></html>";
	public static final String LABEL_INTERFACE_RMI_OFF = "<html><font color='#b0b0b0'><strong>RMI</strong></font></html>";
	public static final String LABEL_INTERFACE_RMI_ON = "<html><font color='#00b000'><strong>RMI</strong></font></html>";
	public static final String LABEL_RAW_LOGS = "Raw Logs: ";
	public static final String LABEL_IN_RAW_LOG_OFF = "<html><font color='#b0b0b0'><strong>RECV</strong></font></html>";
	public static final String LABEL_IN_RAW_LOG_ON = "<html><font color='#00b000'><strong>RECV</strong></font></html>";
	public static final String LABEL_OUT_RAW_LOG_OFF = "<html><font color='#b0b0b0'><strong>SEND</strong></font></html>";
	public static final String LABEL_OUT_RAW_LOG_ON = "<html><font color='#00b000'><strong>SEND</strong></font></html>";

	public static final String BORDER_MOBILE_INFORMATION = "Mobile Information";
	public static final String BORDER_INCOMING_MESSAGES = "Last Incoming SMS";
	public static final String BORDER_OUTGOING_MESSAGES = "Last Outgoing SMS";
	public static final String BORDER_STATISTICS = "Session Statistics";
	public static final String BORDER_AUTHOR = "Author Information";

	public static final String STATUS_LOADING_CONFIG = "Loading Configuration...";
	public static final String STATUS_DISCONNECTED = "Disconnected";
	public static final String STATUS_CONNECTED = "Connected";
	public static final String STATUS_TRY_TO_CONNECT = "Trying to connect...";
	public static final String STATUS_REFRESHING = "Refreshing...";
	public static final String STATUS_PROCESS_IN = "Processing Incoming SMS...";
	public static final String STATUS_PROCESS_OUT = "Processing Outgoing SMS...";
	public static final String STATUS_IDLE = "Idle (connected)";

	public static final String ERROR_TITLE = "Error!";
	public static final String ERROR_CANNOT_CONNECT = "Cannot connect to GSM Device, error: ";
	public static final String ERROR_CANNOT_DISABLE_INDICATIONS = "Cannot disable indications from GSM device.";
	public static final String ERROR_INVALID_PIN = "The given PIN is not accepted. Please check your configuration file.";
	public static final String ERROR_NO_PIN = "No PIN is given but the GSM device requests for one. Please check your configuration file.";
	public static final String ERROR_NO_PDU_SUPPORT = "The GSM device does not support PDU mode.";
	public static final String ERROR_CANNOT_OPEN_DATABASE = "Could not open database. Please check your configuration file.";

	public static final String TEXT_NOT_AVAILABLE = "N/A";
	public static final String TEXT_ZERO = "0";
	public static final String TEXT_NOT_REPORTED = "*NOT REPORTED*";
	public static final String TEXT_CONSOLE = "Using console mode! (See configuration file)";
	public static final String TEXT_INFO = "GSM Device Information";
	public static final String TEXT_INMSG = "Incoming Message:";
	public static final String TEXT_OUTMSG = "Outgoing Message:";
	public static final String TEXT_CONNECTING = "Attemping to connect, please wait...";
}
