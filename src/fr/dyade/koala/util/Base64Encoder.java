/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * BASE64Encoder.java
 * $Id$
 */
package fr.dyade.koala.util;

import java.io.OutputStream;
import java.io.IOException;

/**
 * @version $Revision$
 * @author  Philippe Le Hegaret
 */
public class Base64Encoder {

    final static int encoding[] =
    {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',         // 0-7
        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',         // 8-15
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',         // 16-23
        'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',         // 24-31
        'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',         // 32-39
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v',         // 40-47
        'w', 'x', 'y', 'z', '0', '1', '2', '3',         // 48-55
        '4', '5', '6', '7', '8', '9', '+', '/',         // 56-63
        '='                                             // 64
    };

    private static void encodeAtom(OutputStream out, byte[] data,
				   int offset, int len)
	    throws IOException {

        byte a, b, c;

        if (len == 1) {
            a = data[offset];
            b = 0;
            c = 0;
            out.write(encoding[(a >>> 2) & 0x3F]);
            out.write(encoding[((a << 4) & 0x30) + ((b >>> 4) & 0xf)]);
            out.write('=');
            out.write('=');
        } else if (len == 2) {
            a = data[offset];
            b = data[offset+1];
            c = 0;
            out.write(encoding[(a >>> 2) & 0x3F]);
            out.write(encoding[((a << 4) & 0x30) + ((b >>> 4) & 0xf)]);
            out.write(encoding[((b << 2) & 0x3c) + ((c >>> 6) & 0x3)]);
            out.write('=');
        } else {
            a = data[offset];
            b = data[offset+1];
            c = data[offset+2];
            out.write(encoding[(a >>> 2) & 0x3F]);
            out.write(encoding[((a << 4) & 0x30) + ((b >>> 4) & 0xf)]);
            out.write(encoding[((b << 2) & 0x3c) + ((c >>> 6) & 0x3)]);
            out.write(encoding[c & 0x3F]);
        }
    }

    public static void encode(OutputStream out, byte[] data,
			      int offset, int length)
	    throws IOException {
	int     j;
        int     numBytes;
        byte    tmpbuffer[] = new byte[57];

        while (true) {
	    if (length > 57) {
		numBytes = 57;
		length -= 57;
	    } else {
		numBytes = length;
		length = 0;
	    }
            if (numBytes == 0) {
                break;
            }
            for (j = 0; j < numBytes; j += 3) {
                if ((j + 3) <= numBytes) {
                    encodeAtom(out, data, j+offset, 3);
                } else {
                    encodeAtom(out, data, j+offset, numBytes-j);
                }
            }
            out.write('\n');
            if (numBytes < 57) {
                break;
            }
        }
    }

    public static void encode(OutputStream out, String s)
	    throws IOException {
	byte[] data = new byte[s.length()];
        s.getBytes();
	//s.getBytes(0, data.length, data, 0);
	Base64Encoder.encode(System.out, data, 0, data.length);
    }

}
