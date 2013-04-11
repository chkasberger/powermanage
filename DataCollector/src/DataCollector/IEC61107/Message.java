/*******************************************************************************
 * Copyright (c) 2013 Christian Kasberger.
 * All rights reserved. This program and the accompanying materials are made available for non comercial use!
 *******************************************************************************/
package DataCollector.IEC61107;

public class Message {
	final static byte ACK = 0x06;
	final static byte NAK = 0x15; //
	final static byte STX = 0x0F; // block start
	final static byte ETX = 0x03; // block end
	final static byte START = 0x2F; // /
	final static byte STOP = 0x21; // !
	final static byte TRC = 0x3F;
	final static byte CR = 0x0D;
	final static byte LF = 0x0A;
	byte BCC;

	/** Mode B baud rate definition */
	enum ModeB {
		MB_600, MB_1200, MB_2400, MB_4800, MB_9600, MB_RESERVED_F, MB_RESERVED_G, MB_RESERVED_H, MB_RESERVED_I
	}

	final static byte MB_600 = 0x41, MB_1200 = 0x42, MB_2400 = 0x43,
			MB_4800 = 0x44, MB_9600 = 0x45, MB_RESERVED_F = 0x46,
			MB_RESERVED_G = 0x47, MB_RESERVED_H = 0x48, MB_RESERVED_I = 0x49; // Mode
																				// C
																				// baud
																				// rates
	final static byte MC_300 = 0x30, MC_600 = 0x31, MC_1200 = 0x32,
			MC_2400 = 0x33, MC_4800 = 0x34, MC_9600 = 0x35, MC_19200 = 0x36,
			MC_RESERVED7 = 0x37, MC_RESERVED8 = 0x38, MC_115200 = 0x39; // Mode
																		// B
																		// baud
																		// rates
	final static byte MD_2400 = 0x33;

	/** Protocol control character definition */
	enum PCC {
		NORMAL, SECONDARY, HDLC
	}// , PCC_RESERVED3, PCC_RESERVED4, PCC_RESERVED5, PCC_RESERVED6,
		// PCC_RESERVED7, PCC_RESERVED8, PCC_RESERVED9; // Protocol control
		// character

	final static int PCC_NORMAL = 0x30, PCC_SECONDARY = 0x31, PCC_HDLC = 0x32;// ,
																				// PCC_RESERVED3,
																				// PCC_RESERVED4,
																				// PCC_RESERVED5,
																				// PCC_RESERVED6,
																				// PCC_RESERVED7,
																				// PCC_RESERVED8,
																				// PCC_RESERVED9;
																				// //
																				// Protocol
																				// control
																				// character
	byte pcc;

	/** Mode control character definition */
	enum MCC {
		MCC_READ, MCC_PROGRAMM, MCC_HDLC
	}

	final static int MCC_READ = 0x30, MCC_PROGRAMM = 0x31, MCC_HDLC = 0x32;// ,
																			// MCC_RESERVED3=0x33,
																			// MCC_RESERVED4,
																			// MCC_RESERVED5,
																			// MCC_VENDORSPECIFIC6,
																			// MCC_VENDORSPECIFIC7,
																			// MCC_VENDORSPECIFIC8,
																			// MCC_VENDORSPECIFIC9;
																			// //
																			// Mode
																			// control
																			// character
	byte mcc;

	final byte[] Request = new byte[] { START, TRC, STOP, CR, LF };

	public byte[] ACKNOWLEDGE(Object[] args) {

		switch ((PCC) args[0]) {
		case NORMAL:
			pcc = PCC_NORMAL;
			break;
		case SECONDARY:
			pcc = PCC_SECONDARY;
			break;
		case HDLC:
			pcc = PCC_HDLC;
			break;
		default:
			pcc = PCC_NORMAL;
		}

		switch ((MCC) args[2]) {
		case MCC_READ:
			mcc = MCC_READ;
			break;
		case MCC_PROGRAMM:
			mcc = MCC_PROGRAMM;
			break;
		case MCC_HDLC:
			mcc = MCC_HDLC;
			break;
		default:
			mcc = MCC_READ;
		}

		// return new byte[]{ACK, PCC_NORMAL, MC_2400, MCC_READ, CR, LF};
		return new byte[] { ACK, pcc, (byte) args[1], mcc, CR, LF };
	}

}