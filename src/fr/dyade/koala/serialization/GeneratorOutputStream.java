/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * GeneratorOutputStream.java
 * $Id$
 */
package fr.dyade.koala.serialization;

import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.UTFDataFormatException;

import java.util.Hashtable;

/**
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
public class GeneratorOutputStream extends OutputStream
        implements ObjectOutputHandler, ObjectStreamConstants {

    private OutputStream out;

    private static final boolean KoalaDebug = false;

    private Hashtable classes;

    /**
     * this is a fake object.
     * Only String and ClassDescription are constructed.
     */
    public static final Object fakeObject = new Object();

    private int currentHandle = baseWireHandle;    


    // manage and remap all new handles
    private int[]            ids = new int[1024]; 

    private byte[]           buf; // byte array of buffered data
    boolean                  inBlockDataMode;

    int                      count; // count of bytes in the buffer

    /**
     * Creates a new GeneratorOutputStream
     *
     * @param out the output stream
     */
    public GeneratorOutputStream(OutputStream out) throws IOException {
        this.out     = new BufferedOutputStream(out, 262144);;
	this.classes = new Hashtable();
	this.buf     = new byte[1024];
    }

    /**
     * Notify the start of the serialization.
     *
     * @exception IOException If an I/O error occurs
     */    
    public void writeStartDocument() throws IOException {
        if (KoalaDebug) {
            System.err.println("writeStreamHeader()");
        }
        write(STREAM_MAGIC, false, null);
        write(STREAM_VERSION, false, null);
     }
    
    /**
     * Notify the end of the serialization.
     *
     * @exception IOException If an I/O error occurs
     */    
    public void writeEndDocument() throws IOException {
	// nothing to do
    }

    /**
     * Write the start of an object.
     *
     * @param id the unique of this object.
     * @param _class the class description of this object.
     * @param isTransient <code>true</code> if this boolean is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void writeStartObject(int id, 
				 ClassDescription _class, 
				 boolean isTransient,
				 Field field)
	    throws IOException {
 	if (KoalaDebug) {
	    System.err.println("writeObject(" + _class + ")");
	}	
	drain();
	out.write(TC_OBJECT);
	writeClassDescriptionInternal(_class);
	int newId = newHandle();
	remapHandle(id, newId);
    }

    /**
     * Write the end of an object.
     *
     * @param _super the class description of the super instance.
     * @exception IOException If an I/O error occurs
     */    
    public void writeEndObject(int id, ClassDescription _class, Field field) 
	    throws IOException {
 	if (KoalaDebug) {
	    System.err.println("writeEndObject(" + _class + ")");
	}
	drain();
	if (_class.hasWriteMethod()) {
	    out.write(TC_ENDBLOCKDATA);
	}
    }

    /**
     * Write the start of a class object.
     *
     * @param id the unique of this object.
     * @param _class the class description of this object.
     * @param isTransient <code>true</code> if this boolean is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void writeStartObjectClass(int id, 
				      ClassDescription _class, 
				      boolean isTransient,
				      Field field)
	throws IOException {
 	if (KoalaDebug) {
	    System.err.println("writeObjectClass(" + _class + ")");
	}	
	drain();
	out.write(TC_CLASS);
	writeClassDescriptionInternal(_class);
	int newId = newHandle();
	remapHandle(id, newId);
    }

    /**
     * Write the end of a class object.
     *
     * @param _super the class description of the super instance.
     * @exception IOException If an I/O error occurs
     */    
    public void writeEndObjectClass(int id, 
				    ClassDescription _class,
				    Field field) 
	    throws IOException {
 	if (KoalaDebug) {
	    System.err.println("writeEndObjectClass(" + _class + ")");
	}
	drain();
    }

    /**
     * Write the start of a super instance.
     *
     * @param _super the class description of the super instance.
     * @exception IOException If an I/O error occurs
     */    
    public void writeStartSuper(ClassDescription _super) throws IOException {
	// nothing to do
    }

    /**
     * Write the end of the super instance.
     *
     * @exception IOException If an I/O error occurs
     */    
    public void writeEndSuper(ClassDescription _super) throws IOException {
	// nothing to do
 	if (KoalaDebug) {
	    System.err.println("writeEndSuper(" + _super + ")");
	}
	drain();
	if (_super.hasWriteMethod()) {
	    out.write(TC_ENDBLOCKDATA);
	}
    }

    /**
     * Write the start of an array.
     *
     * @param id the unique of this array.
     * @param _class the class description of this array.
     * @param size the size of this array.
     * @param isTransient <code>true</code> if this boolean is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void writeStartArray(int id, ClassDescription _class, 
			 int size, boolean isTransient, Field field)
	throws IOException {
 	if (KoalaDebug) {
	    System.err.println("writeArray(" + _class + ")");
	}	
	drain();
	out.write(TC_ARRAY);
	writeClassDescriptionInternal(_class);
	int newId = newHandle();
	remapHandle(id, newId);
	write(size, false, null);
    }

    /**
     * Write the end of an array.
     *
     * @param id the unique id of this array.
     * @exception IOException If an I/O error occurs
     */    
    public void writeEndArray(int id, ClassDescription _class, Field field) 
	    throws IOException {
	// nothing to do
    }

    /**
     * Write a null reference to an object or an array.
     *
     * @exception IOException If an I/O error occurs
     */
    public void writeNullReference(Field field) throws IOException {
 	if (KoalaDebug) {
	    System.err.println("writeNull()");
	}	
	drain();
	out.write(TC_NULL);
    }

    /**
     * Write a reference to an object or an array.
     *
     * @param id the id reference.
     * @exception IOException If an I/O error occurs
     */    
    public void writeReference(int id, Field field) throws IOException {
 	if (KoalaDebug) {
	    System.err.println("writeReference(" + getId(id) + ")");
	}		
	drain();
	out.write(TC_REFERENCE);
	write(getId(id), false, null);
    }

    /**
     * Write a new class description.
     * set the unique id to zero !
     *
     * @param _class the new class description
     * @exception IOException If an I/O error occurs
     */    
    public void writeClassDescription(ClassDescription _class) {
	// do nothing
    }

    /**
     * Write a new class description.
     *
     * @param _class the new class description
     * @exception IOException If an I/O error occurs
     */    
    private void writeClassDescriptionInternal(ClassDescription _class) 
	    throws IOException {
	OutputStream out = this.out;

	if (_class == null) {
	    write(TC_NULL);
	    return;
	} else if (classes.get(_class.getName()) != null) {
	    out.write(TC_REFERENCE);
	    write(_class.getId(), false, null);
	    return;
	}
	classes.put(_class.getName(), _class);
	int id = _class.getId();

	if (KoalaDebug) {
	    System.err.println("writeClass(" + _class.getName() + ")");
	}

	Field[] fs = _class.getFields();
	Type ct = _class.getType();

	out.write(TC_CLASSDESC);
	if (ct.isArray()) {
	    try {
		writeUTF(ct.toStringInternal().replace('/', '.'));
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	} else {
	    try {
		writeUTF(ct.getName());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	write(_class.getSerialVersionUID(), false, null);

	id = newHandle();
	_class.setId(id);

        byte classDescFlags = 0;
        if (_class.hasWriteMethod()) {
            classDescFlags |= SC_WRITE_METHOD;
        }
        if (_class.isSerializable()) {
            classDescFlags |= SC_SERIALIZABLE;
        }
        if (_class.isExternalizable()) {
            classDescFlags |= SC_EXTERNALIZABLE;
        }
	out.write(classDescFlags);
	if (fs == null) {
	    out.write(0);
	    out.write(0);
	} else {
	    int v = fs.length;
	    out.write((v >>> 8) & 0xFF);
	    out.write((v >>> 0) & 0xFF);
	}

	if (fs != null) {
	    for (int i = 0; i < fs.length; i++) {
		Type t = fs[i].getType();
		switch (t.getTypeDefinition()) {
		case Type.ARRAY:
		    out.write(ARRAY_TYPE);
		    writeUTF(fs[i].getName());
		    write(t.toStringInternal(), false, null);
		    break;
		case Type.OBJECT:
		case Type.STRING:
		    out.write(OBJECT_TYPE);
		    writeUTF(fs[i].getName());
		    write(t.toStringInternal(), false, null);
		    break;
		case Type.BYTE:
		    out.write(BYTE_TYPE);		    
		    writeUTF(fs[i].getName());
		    break;
		case Type.CHAR:
		    out.write(CHAR_TYPE);		    
		    writeUTF(fs[i].getName());
		    break;
		case Type.DOUBLE:
		    out.write(DOUBLE_TYPE);		    
		    writeUTF(fs[i].getName());
		    break;
		case Type.FLOAT:
		    out.write(FLOAT_TYPE);		    
		    writeUTF(fs[i].getName());
		    break;
		case Type.INT:
		    out.write(INT_TYPE);		    
		    writeUTF(fs[i].getName());
		    break;
		case Type.LONG:
		    out.write(LONG_TYPE);		    
		    writeUTF(fs[i].getName());
		    break;
		case Type.SHORT:
		    out.write(SHORT_TYPE);		    
		    writeUTF(fs[i].getName());
		    break;
		case Type.BOOLEAN:
		    out.write(BOOLEAN_TYPE);
		    writeUTF(fs[i].getName());
		    break;
		}
	    }
	}
	out.write(TC_ENDBLOCKDATA);
	writeClassDescriptionInternal(_class.getSuperClass());
    }

    /**
     * Write a string with a specified name.
     *
     * @param value the value of this string
     * @param isTransient <code>true</code> if this string is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void write(String value, boolean isTransient, Field field)
	throws IOException {
	if (KoalaDebug) {
	    System.err.println("writeString(\"" + value 
			       + "\", " + isTransient + ")");
	}
	drain();
	out.write(TC_STRING);
	newHandle();
	writeUTF(value);
    }
    
    /**
     * Write a boolean with a specified name.
     *
     * @param value the value of this boolean
     * @param isTransient <code>true</code> if this boolean is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void write(boolean value, boolean isTransient, Field field)
	throws IOException {
	if (KoalaDebug) {
	    System.err.println("writeBoolean(" + value 
			       + ", " + isTransient + ")");
	}
	if (!isTransient) {
	    drain();
	    out.write(value ? 1 : 0);
	} else {
	    writeBlockByte(value ? 1 : 0);
	}
    }
    
    /**
     * Write a byte with a specified name.
     *
     * @param value the value of this byte
     * @param isTransient <code>true</code> if this byte is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void write(byte value, boolean isTransient, Field field)
	throws IOException {
	if (KoalaDebug) {
	    System.err.println("writeByte(" + value 
			       + ", " + isTransient + ")");
	}
	if (!isTransient) {
	    drain();
	    out.write(value);
	} else {
	    writeBlockByte(value);
	}
    }
    
    /**
     * Write a char with a specified name.
     *
     * @param value the value of this char
     * @param isTransient <code>true</code> if this char is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void write(char value, boolean isTransient, Field field)
	throws IOException {
	if (KoalaDebug) {
	    System.err.println("writeChar(" + value 
			       + ", " + isTransient + ")");
	}	
	if (!isTransient) {
	    drain();
	    out.write((value >>> 8) & 0xFF);
	    out.write((value >>> 0) & 0xFF);
	} else {
	    writeBlockByte((value >>> 8) & 0xFF);
	    writeBlockByte((value >>> 0) & 0xFF);
	}
    }
    
    /**
     * Write a short with a specified name.
     *
     * @param value the value of this short
     * @param isTransient <code>true</code> if this short is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void write(short value, boolean isTransient, Field field)
	throws IOException {
	if (KoalaDebug) {
	    System.err.println("writeShort(" + value 
			       + ", " + isTransient + ")");
	}	
 	if (!isTransient) {
	    drain();
	    out.write((value >>> 8) & 0xFF);
	    out.write((value >>> 0) & 0xFF);
	} else {
	    writeBlockByte((value >>> 8) & 0xFF);
	    writeBlockByte((value >>> 0) & 0xFF);
	}
    }
    
    /**
     * Write an int with a specified name.
     *
     * @param value the value of this int
     * @param isTransient <code>true</code> if this int is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void write(int value, boolean isTransient, Field field)
	throws IOException {
	if (KoalaDebug) {
	    System.err.println("writeInt(" + value 
			       + ", " + isTransient + ")");
	}	
 	if (!isTransient) {
	    drain();
	    out.write((value >>> 24) & 0xFF);
	    out.write((value >>> 16) & 0xFF);
	    out.write((value >>>  8) & 0xFF);
	    out.write((value >>>  0) & 0xFF);
	} else {	    
	    writeBlockByte((value >>> 24) & 0xFF);
	    writeBlockByte((value >>> 16) & 0xFF);
	    writeBlockByte((value >>>  8) & 0xFF);
	    writeBlockByte((value >>>  0) & 0xFF);
	}
    }
    
    /**
     * Write a long with a specified name.
     *
     * @param value the value of this long
     * @param isTransient <code>true</code> if this long is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void write(long value, boolean isTransient, Field field)
	throws IOException {
 	if (KoalaDebug) {
	    System.err.println("writeLong(" + value 
			       + ", " + isTransient + ")");
	}	
 	if (!isTransient) {
	    drain();
	    out.write((int)(value >>> 56) & 0xFF);
	    out.write((int)(value >>> 48) & 0xFF);
	    out.write((int)(value >>> 40) & 0xFF);
	    out.write((int)(value >>> 32) & 0xFF);
	    out.write((int)(value >>> 24) & 0xFF);
	    out.write((int)(value >>> 16) & 0xFF);
	    out.write((int)(value >>>  8) & 0xFF);
	    out.write((int)(value >>>  0) & 0xFF);
	} else {
	    writeBlockByte((int)(value >>> 56) & 0xFF);
	    writeBlockByte((int)(value >>> 48) & 0xFF);
	    writeBlockByte((int)(value >>> 40) & 0xFF);
	    writeBlockByte((int)(value >>> 32) & 0xFF);
	    writeBlockByte((int)(value >>> 24) & 0xFF);
	    writeBlockByte((int)(value >>> 16) & 0xFF);
	    writeBlockByte((int)(value >>>  8) & 0xFF);
	    writeBlockByte((int)(value >>>  0) & 0xFF);
	}
    }
    
    /**
     * Write a float with a specified name.
     *
     * @param value the value of this float
     * @param isTransient <code>true</code> if this float is transient.
     * @exception IOException If an I/O error occurs
    */    
    public void write(float value, boolean isTransient, Field field)
	throws IOException {
 	if (KoalaDebug) {
	    System.err.println("writeFloat(" + value 
			       + ", " + isTransient + ")");
	}	
	int v = Float.floatToIntBits(value);
 	if (!isTransient) {
	    drain();
	    out.write((v >>> 24) & 0xFF);
	    out.write((v >>> 16) & 0xFF);
	    out.write((v >>>  8) & 0xFF);
	    out.write((v >>>  0) & 0xFF);
	} else {
	    writeBlockByte((v >>> 24) & 0xFF);
	    writeBlockByte((v >>> 16) & 0xFF);
	    writeBlockByte((v >>>  8) & 0xFF);
	    writeBlockByte((v >>>  0) & 0xFF);
	}
    }
    
    /**
     * Write a double with a specified name.
     *
     * @param value the value of this double
     * @param isTransient <code>true</code> if this double is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void write(double value, boolean isTransient, Field field)
	throws IOException {
  	if (KoalaDebug) {
	    System.err.println("writeDouble(" + value 
			       + ", " + isTransient + ")");
	}	
	long v = Double.doubleToLongBits(value);
 	if (!isTransient) {
	    drain();
	    out.write((int)(v >>> 56) & 0xFF);
	    out.write((int)(v >>> 48) & 0xFF);
	    out.write((int)(v >>> 40) & 0xFF);
	    out.write((int)(v >>> 32) & 0xFF);
	    out.write((int)(v >>> 24) & 0xFF);
	    out.write((int)(v >>> 16) & 0xFF);
	    out.write((int)(v >>>  8) & 0xFF);
	    out.write((int)(v >>>  0) & 0xFF);
	} else {
	    writeBlockByte((int)(v >>> 56) & 0xFF);
	    writeBlockByte((int)(v >>> 48) & 0xFF);
	    writeBlockByte((int)(v >>> 40) & 0xFF);
	    writeBlockByte((int)(v >>> 32) & 0xFF);
	    writeBlockByte((int)(v >>> 24) & 0xFF);
	    writeBlockByte((int)(v >>> 16) & 0xFF);
	    writeBlockByte((int)(v >>>  8) & 0xFF);
	    writeBlockByte((int)(v >>>  0) & 0xFF);
	}
    }

    /**
     * Writes a string to the underlying output stream using UTF-8 
     * encoding in a machine-independent manner. 
     * <p>
     * First, two bytes are written to the output stream as if by the 
     * <code>writeShort</code> method giving the number of bytes to 
     * follow. This value is the number of bytes actually written out, 
     * not the length of the string. Following the length, each character 
     * of the string is output, in sequence, using the UTF-8 encoding 
     * for the character. 
     *
     * @param      str   a string to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    private void writeUTF(String str) throws IOException {
        OutputStream out = this.out;
        int strlen = str.length();
        int utflen = 0;

        for (int i = 0 ; i < strlen ; i++) {
            int c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                utflen++;
            } else if (c > 0x07FF) {
                utflen += 3;
            } else {
                utflen += 2;
            }
        }

        if (utflen > 65535) {
            throw new UTFDataFormatException();
	}
        out.write((utflen >>> 8) & 0xFF);
        out.write((utflen >>> 0) & 0xFF);
        for (int i = 0 ; i < strlen ; i++) {
            int c = str.charAt(i);
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
     * Write some escape data.
     * When the generator can determine the type of the input, it generates
     * some "escape" data to preserve the integrity of the object.
     *
     * @param data the array of bytes.
     * @param offset the offset in the array of bytes
     * @param length the length of bytes to read from the offset.
     * @exception IOException If an I/O error occurs 
     */
    public void writeRow(byte[] data, int offset, int length) 
	    throws IOException {
 	if (KoalaDebug) {
	    System.err.println("writeRow( " + length + ")");
	}	
	drain();
	if (length != 0) {
	    if (length <= 255) {
		out.write(TC_BLOCKDATA);
		out.write(length);
	    } else {
		// use block data with int size if necessary
		out.write(TC_BLOCKDATALONG);
		// send 32 bit int directly to underlying stream
		out.write((length >> 24) & 0xFF);
		out.write((length >> 16) & 0xFF);
		out.write((length >>  8) & 0xFF);
		out.write(length & 0xFF);
	    }
	    out.write(data, 0, length);
	}
    }

    public void write(byte[] data, int offset, int length) throws IOException {
	if (KoalaDebug) {
	    int le = offset + length;
	    System.err.print("print data ");
	    for (int i = offset; i < le; i++) {
		System.err.print(Integer.toString(i, 16));
	    }
	    System.err.print('\n');
	}
	drain();
	out.write(data, offset, length);
    }

    public void write(int i) throws IOException {
	if (KoalaDebug) {
	    System.err.println( "print " + Integer.toString(i, 16));
	}
	drain();
	out.write(i);
    }

    public void write(byte[] data) throws IOException {
	if (KoalaDebug) {
	    System.err.print("print data ");
	    for (int i = 0; i < data.length; i++) {
		System.err.print(Integer.toString(i, 16));
	    }
	    System.err.print('\n');
	}
	drain();
	out.write(data, 0, data.length);
    }

    /**
     * Flushes the object output handler
     * @exception IOException If an I/O error occurs 
     */    
    public void flush() throws IOException {
	drain();
	out.flush();
    }

    /**
     * Close this output stream and releases any system ressources associated 
     * with this stream.
     * @exception IOException If an I/O error occurs 
     */    
    public void close() throws IOException {
	flush();
	out.close();
    }

    private int newHandle() {
        return currentHandle++;
    }

    private int getId(int predId) {
	if (predId >= ids.length) {
	    throw new IllegalStateException("invalid id " + predId);
	}
	return ids[predId];
    }

    private void remapHandle(int predId, int newId) {
	if (predId >= ids.length) {
            int[] old = ids;
            ids = new int[predId + 1000];
            System.arraycopy(old, 0, ids, 0, old.length);
        }
	ids[predId] = newId;
    }

    private void writeBlockByte(int b) throws IOException {
	if (count == buf.length) {
	    drain();
	}
	buf[count++] = (byte) b;
    }

    private final void drain() throws IOException {
	if (count != 0) {
	    if (count <= 255) {
		out.write(TC_BLOCKDATA);
		out.write(count);
	    } else {
		// use block data with int size if necessary
		out.write(TC_BLOCKDATALONG);
		// send 32 bit int directly to underlying stream
		out.write((count >> 24) & 0xFF);
		out.write((count >> 16) & 0xFF);
		out.write((count >>  8) & 0xFF);
		out.write(count & 0xFF);
	    }
	    out.write(buf, 0, count);
	    count = 0;
	}
    }

}
