/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * Base64Decoder.java
 * $Id$
 */
package fr.dyade.koala.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * @version $Revision$
 * @author  Philippe Le Hegaret
 */
public class Base64Decoder {

    private static int get1(byte buf[], int off) {
        return ((buf[off] & 0x3f) << 2) | ((buf[off+1] & 0x30) >>> 4) ;
    }

    private static int get2(byte buf[], int off) {
        return ((buf[off+1] & 0x0f) << 4) | ((buf[off+2] &0x3c) >>> 2) ;
    }

    private static int get3(byte buf[], int off) {
        return ((buf[off+2] & 0x03) << 6) | (buf[off+3] & 0x3f) ;
    }

    private static int check(int ch) {
        if ((ch >= 'A') && (ch <= 'Z')) {
            return ch - 'A' ;
        } else if ((ch >= 'a') && (ch <= 'z')) {
            return ch - 'a' + 26 ;
        } else if ((ch >= '0') && (ch <= '9')) {
            return ch - '0' + 52 ;
        } else {
            switch (ch) {
              case '=':
                  return 65 ;
              case '+':
                  return 62 ;
              case '/':
                  return 63 ;
              default:
                  return -1 ;
            }
        }
    }

    /**
     * Do the actual decoding.
     * Process the input stream by decoding it and emiting the resulting bytes
     * into the output stream.
     * @exception IOException If the input or output stream accesses failed.
     * @exception Base64FormatException If the input stream is not compliant
     *    with the BASE64 specification.
     */
    public static void decode(OutputStream out,
			      byte[] data, int offset, int length) 
            throws IOException, Base64FormatException {
        byte chunk[]  = new byte[4];
        int  got      = length;
        int  ready    = 0 ;
        
        if (got > 0) {
            int skiped = 0;
            while (skiped < got) {
                // Check for un-understood characters:
                while ((ready < 4) && (skiped < got)) {
                    int ch = check(data[offset + skiped++]) ;
                    if (ch >= 0) {
                        chunk[ready++] = (byte) ch ;
		    }
                }
		if (skiped == got) {
		    break;
		}
                if (chunk[2] == 65) {
                    out.write(get1(chunk, 0));
                    return ;
                } else if (chunk[3] == 65) {
                    out.write(get1(chunk, 0)) ;
                    out.write(get2(chunk, 0)) ;
                    return ;
                } else {
                    out.write(get1(chunk, 0)) ;
                    out.write(get2(chunk, 0)) ;
                    out.write(get3(chunk, 0)) ;
                }
                ready = 0 ;
            } 
        }
        if (ready != 0) {
            throw new Base64FormatException("Invalid length.");
	}
        out.flush() ;
    }

}
