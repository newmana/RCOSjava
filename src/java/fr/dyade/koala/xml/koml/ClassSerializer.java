/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * ClassSerializer.java
 * $Id$
 */
package fr.dyade.koala.xml.koml;

import fr.dyade.koala.xml.koml.KOMLConstants;
import fr.dyade.koala.serialization.ObjectOutputHandler;
import fr.dyade.koala.serialization.ClassDescription;
import fr.dyade.koala.serialization.Field;

import fr.dyade.koala.util.Base64Encoder;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.io.BufferedOutputStream;
import java.io.OutputStream;

/**
 * This class transform all class description from the generator
 * in an XML document.
 * <p>Here is an example :</p>
 * <pre class="example">
 *  <class name='java.lang.Boolean' uid='-3665804199014368530'>
 *   <field name='value' type='boolean'/>
 *  </class>
 * </pre>
 *
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
abstract class ClassSerializer
        implements ObjectOutputHandler {

    private OutputStream out;

    /**
     * Creates a new class description XML file.
     */
    public ClassSerializer(OutputStream out) throws IOException {
	this.out = new BufferedOutputStream(out);
    }

    void write(String s) throws IOException {
        int strlen = s.length();

        for (int i = 0 ; i < strlen ; i++) {
            int c = s.charAt(i);
	    // @@SEEME
	    // is it really possible to have 0x0001 ?
            if ((c >= 0x0001) && (c <= 0x007F)) {
                out.write(c);
            } else if (c > 0x07FF) {
                out.write(0xE0 | ((c >> 12) & 0x0F));
                out.write(0x80 | ((c >>  6) & 0x3F));
                out.write(0x80 | ((c >>  0) & 0x3F));
            } else {
                out.write(0xC0 | ((c >>  6) & 0x1F));
                out.write(0x80 | ((c >>  0) & 0x3F));
            }
        }
    }

    /**
     * All possible chars for representing a number as a String
     */
    private final static byte[] digits = {
        (byte) '0' , (byte) '1' , (byte) '2' , (byte) '3' , (byte) '4' ,
	(byte) '5' , (byte) '6' , (byte) '7' , (byte) '8' , (byte) '9' ,
	(byte) 'a' , (byte) 'b' , (byte) 'c' , (byte) 'd' , (byte) 'e' ,
	(byte) 'f' , (byte) 'g' , (byte) 'h' , (byte) 'i' , (byte) 'j' ,
	(byte) 'k' , (byte) 'l' , (byte) 'm' , (byte) 'n' , (byte) 'o' ,
	(byte) 'p' , (byte) 'q' , (byte) 'r' , (byte) 's' , (byte) 't' ,
        (byte) 'u' , (byte) 'v' , (byte) 'w' , (byte) 'x' , (byte) 'y' ,
	(byte) 'z'
    };

    void writeUnicode(int value) throws IOException {
	byte buf[] = new byte[33];
	buf[32] = buf[31] = buf[30] = buf[29] = (byte) '0';
	buf[28] = (byte) 'u';
	buf[27] = (byte) '\\';
	int i = - ((int) value);
	int charPos = 32;
	while (i <= -16) {
	    buf[charPos--] = digits[-(i % 16)];
	    i = i / 16;
	}
	buf[charPos] = digits[-i];

	out.write(buf, 27, 6);
    }

    void writeProtected(String s) throws IOException {
        int strlen = s.length();

        for (int i = 0 ; i < strlen ; i++) {
            int c = s.charAt(i);

            if ((c >= 0x0000) && (c <= 0x007F)) {
		switch (c) {
		case '\u0000':
		    out.write('\\');
		    out.write('u');
		    out.write('0');
		    out.write('0');
		    out.write('0');
		    out.write('0');
		    break;
		case '\\':
		    out.write('\\');
		    out.write('\\');
		    break;
		case '\n':
		    out.write('\\');
		    out.write('n');
		    break;
		case '\r':
		    out.write('\\');
		    out.write('r');
		    break;
		case '\t':
		    out.write('\\');
		    out.write('t');
		    break;
		case '\b':
		    out.write('\\');
		    out.write('b');
		    break;
		case '\f':
		    out.write('\\');
		    out.write('f');
		    break;
		case '<':
		    out.write('&');
		    out.write('l');
		    out.write('t');
		    out.write(';');
		    break;
		case '&':
		    out.write('&');
		    out.write('a');
		    out.write('m');
		    out.write('p');
		    out.write(';');
		    break;
		default:
		    if (c < 0x0020) {
			writeUnicode(c);
		    } else {
			out.write(c);
		    }
		    break;
		}
            } else if (c > 0x07FF) {
		if ((c <= 0xD7FF)
		    || ((c >= 0xE000) && (c <= 0xFFFD))
		    || ((c >= 0x10000) && (c <= 0x10FFFF))) {
		    out.write(0xE0 | ((c >> 12) & 0x0F));
		    out.write(0x80 | ((c >>  6) & 0x3F));
		    out.write(0x80 | ((c >>  0) & 0x3F));
		} else {
		    writeUnicode(c);
		}
            } else {
                out.write(0xC0 | ((c >>  6) & 0x1F));
                out.write(0x80 | ((c >>  0) & 0x3F));
            }
        }
    }

    void writeBinary(byte[] data, int offset, int length)
	    throws IOException {
	Base64Encoder.encode(out, data, offset, length);
    }

    /**
     * Starts a new class description XML file.
     * The root element is <code>classes</code>
     *
     * @exception IOException If an I/O error occurs
     */
    public void writeStartClasses() throws IOException {
	write("<" + KOMLConstants.CLASSES + ">\n");
    }

    /**
     * Ends the XML file.
     * This methods flushs the internal PrintWriter.
     *
     * @exception IOException If an I/O error occurs
     */
    public void writeEndClasses() throws IOException {
	write("</" + KOMLConstants.CLASSES + ">\n");
	out.flush();
    }

    private void writeFields(Field f) throws IOException {
	write("<" + KOMLConstants.FIELD + " " + KOMLConstants.A_NAME + "='");
	write(f.getName());
	write("' " + KOMLConstants.A_TYPE + "='");
	write(f.getType().toString());
	write("'/>\n");
    }

     /**
      * Write a new class description in the XML file.
      *
      * @param _class the new class description
      * @exception IOException If an I/O error occurs
      */
    void _writeClassDescription(ClassDescription _class)
	    throws IOException {
	write("<" + KOMLConstants.CLASS + " " + KOMLConstants.A_NAME + "='");
	write(_class.getType().toString());
	write("' " + KOMLConstants.A_UID + "='");
	write(Long.toString(_class.getSerialVersionUID()));
	if (_class.getSuperClass() != null) {
	    write("' " + KOMLConstants.A_SUPER + "='");
	    write(_class.getSuperClass().getType().toString());
	}
	if (_class.hasWriteMethod()) {
	    write("' " + KOMLConstants.A_WRITEMETHOD + "='" + KOMLConstants.V_TRUE);
	}
	if (_class.isExternalizable()) {
	    write("' " + KOMLConstants.A_IMPLEMENTS + "='" + KOMLConstants.V_EXTERNALIZABLE);
	}
	write("'>\n");
	Field[] fs = _class.getFields();
	if (fs != null) {
	    for (int i = 0; i < fs.length; i++) {
		writeFields(fs[i]);
	    }
	}
	write("</" + KOMLConstants.CLASS + ">\n");
    }

    public void writeClassDescription(ClassDescription _class)
	    throws IOException {
	// nothing to do !
    }

    /**
     * Close this output stream and releases any system ressources associated
     * with this stream.
     * @exception IOException If an I/O error occurs
     */
    public void close() throws IOException {
	out.close();
    }

    /**
     * Flushes the object output handler.
     * @exception IOException If an I/O error occurs
     */
    public void flush() throws IOException {
	out.flush();
    }

}
