/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * Serializer.java
 * $Id$
 */
package fr.dyade.koala.xml.koml;

import java.io.IOException;
import java.io.OutputStream;
import fr.dyade.koala.serialization.ClassDescription;
import fr.dyade.koala.serialization.Field;

/**
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
class Serializer extends ClassSerializer {

    /**
     * Creates a new XML serializer.
     * 
     * @param instance the output for all instances.
     * @param classDescription the output for all class description.
     */    
    public Serializer(OutputStream out) throws IOException {
	super(out);
    }

    /**
     * Notify the start of the serialization.
     *
     * @exception IOException If an I/O error occurs
     */    
    public void writeStartDocument() throws IOException {
	write("<?xml version='1.0' encoding='UTF-8'?>\n");
	write("<!DOCTYPE " + KOML + " SYSTEM \"" + KOML_DTD + "\">\n");
	write("<" + KOML + " " + A_VERSION + "='" 
		  + MAJOR_VERSION + "." + MINOR_VERSION + "'>\n");
    }

    /**
     * Notify the end of the serialization.
     *
     * @exception IOException If an I/O error occurs
     */    
    public void writeEndDocument() throws IOException {
 	write("</" + KOML + ">\n");
	flush();
    }

    /**
     * Write the start of an object.
     *
     * @param id the unique of this object.
     * @param _class the class description of this object.
     * @param isTransient <code>true</code> if this boolean is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void writeStartObject(int id, ClassDescription _class,
				 boolean isTransient, Field field)
	    throws IOException {
	write("<" + OBJECT + " " + A_CLASS + "='");
	write(_class.getType().toString());
	write("' " + A_ID + "='i");
	write(Integer.toString(id, 10));
	if (field != null) {
	    write("' " + A_NAME + "=\'" + field.getName());
	}
	if (isTransient) {
	    write("' " + A_TRANSIENT + "=\'" + V_TRUE + "'>\n");
	} else {
	    write("'>\n");
	}
    }

    /**
     * Write the end of an object.
     *
     * @param _super the class description of the super instance.
     * @exception IOException If an I/O error occurs
     */    
    public void writeEndObject(int id, ClassDescription _class, Field field) 
	    throws IOException {
	write("</" + OBJECT + ">\n");
    }

    /**
     * Write the start of a class object.
     *
     * @param id the unique of this object.
     * @param _class the class description of this object.
     * @param isTransient <code>true</code> if this boolean is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void writeStartObjectClass(int id, ClassDescription _class,
				      boolean isTransient, Field field)
	    throws IOException {
	write("<" + OBJECT_CLASS + " " + A_CLASS + "='");
	write(_class.getType().toString());
	write("' " + A_ID + "='i");
	write(Integer.toString(id, 10));
	if (field != null) {
	    write("' " + A_NAME + "=\'" + field.getName());
	}
	if (isTransient) {
	    write("' " + A_TRANSIENT + "=\'" + V_TRUE + "'>\n");
	} else {
	    write("'>\n");
	}
    }

    /**
     * Write the end of a class object.
     *
     * @param _super the class description of the super instance.
     * @exception IOException If an I/O error occurs
     */    
    public void writeEndObjectClass(int id, ClassDescription _class,
				    Field field) 
	    throws IOException {
	write("</" + OBJECT_CLASS + ">\n");
    }

    /**
     * Write the start of a super instance.
     *
     * @param _super the class description of the super instance.
     * @exception IOException If an I/O error occurs
     */    
    public void writeStartSuper(ClassDescription _super) throws IOException {
	write("<" + SUPER + " " +  A_CLASS + "='");
	write(_super.getType().toString());
	write("'>\n");
    }

    /**
     * Write the end of the super instance.
     *
     * @exception IOException If an I/O error occurs
     */    
    public void writeEndSuper(ClassDescription _super) throws IOException {
	write("</" + SUPER + ">\n");
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
	write("<" + ARRAY + " " + A_CLASS + "='");
	write(_class.getType().toString());
	write("' " + A_ID + "='i");
	write(Integer.toString(id, 10));
	write("' " + A_LENGTH + "='");
	write(Integer.toString(size, 10));
	if (field != null) {
	    write("' " + A_NAME + "=\'" + field.getName());
	}
	if (isTransient) {
	    write("' " + A_TRANSIENT + "=\'" + V_TRUE + "'>\n");
	} else {
	    write("'>\n");
	}
    }

    /**
     * Write the end of an array.
     *
     * @param id the unique id of this array.
     * @exception IOException If an I/O error occurs
     */    
    public void writeEndArray(int id, ClassDescription _class, Field field) 
	    throws IOException {
	write("</" + ARRAY + ">\n");
    }

    /**
     * Write a null reference to an object or an array.
     *
     * @exception IOException If an I/O error occurs
     */    
    public void writeNullReference(Field field) throws IOException {
	write("<" + NULL);	
 	if (field != null) {
	    write(" " + A_NAME + "=\'" + field.getName() + "'/>\n");
	} else {
	    write("/>\n");
	}
    }

    /**
     * Write a reference to an object or an array.
     *
     * @param id the id reference.
     * @exception IOException If an I/O error occurs
     */    
    public void writeReference(int id, Field field) throws IOException {
	write("<" + REFERENCE + " " + A_REF + "=\'i");
	write(Integer.toString(id, 10));
	if (field != null) {
	    write("' " + A_NAME + "=\'" + field.getName() + "'/>\n");
	} else {
	    write("'/>\n");
	}
    }

    /**
     * Write some escape data.
     * When the generator can't determine the type of the input, it generates
     * some "escape" data to preserve the integrity of the object.
     *
     * @param data the array of bytes.
     * @param offset the offset in the array of bytes
     * @param length the length of bytes to read from the offset.
     * @exception IOException If an I/O error occurs 
     */
    public void writeRow(byte[] data, int offset, int length) 
	    throws IOException {
	write("<" + ROW + " " + A_SIZE + "='");
	write(Integer.toString(length, 10));
	write("'>");
	writeBinary(data, offset, length);
	write("</" + ROW + ">\n");
    }

    private final void write(String type, String value, 
			     boolean isTransient, Field field) 
	    throws IOException {
	write("<" + VALUE + " " + A_TYPE + "='");
	write(type);
	if (field != null) {
	    write("' " + A_NAME + "=\'" + field.getName());
	}
	if (isTransient) {
	    write("' " + A_TRANSIENT + "=\'true'>");
	} else {
	    write("'>");
	}
	writeProtected(value);
	write("</" + VALUE + ">\n");
    }
    
    /**
     * Write a string with a specified name.
     *
     * @param name the name of this string. May be null.
     * @param value the value of this string
     * @param isTransient <code>true</code> if this string is transient.
     * @param id the unique id of this string.
     * @exception IOException If an I/O error occurs
     */    
    public void write(String value, boolean isTransient, Field field)
	    throws IOException {
	write(V_STRING, value, isTransient, field);
    }
    
    /**
     * Write a boolean with a specified name.
     *
     * @param name the name of this boolean. May be null.
     * @param value the value of this boolean
     * @param isTransient <code>true</code> if this boolean is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void write(boolean value, boolean isTransient, Field field)
	    throws IOException {
	write(V_BOOLEAN, (value)?V_TRUE:V_FALSE, isTransient, field);
    }
    
    /**
     * Write a byte with a specified name.
     *
     * @param name the name of this byte. May be null.
     * @param value the value of this byte
     * @param isTransient <code>true</code> if this byte is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void write(byte value, boolean isTransient, Field field)
	    throws IOException {
	write(V_BYTE, Byte.toString(value), isTransient, field);
    }
    
    /**
     * Write a char with a specified name.
     *
     * @param name the name of this char. May be null.
     * @param value the value of this char
     * @param isTransient <code>true</code> if this char is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void write(char value, boolean isTransient, Field field)
	    throws IOException {
	write(V_CHAR, new Character(value).toString(), isTransient, field);
    }
 
    /**
     * Write a short with a specified name.
     *
     * @param name the name of this short. May be null.
     * @param value the value of this short
     * @param isTransient <code>true</code> if this short is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void write(short value, boolean isTransient, Field field)
	throws IOException {
	write(V_SHORT, Short.toString(value), isTransient, field);
    }
    
    /**
     * Write an int with a specified name.
     *
     * @param name the name of this int. May be null.
     * @param value the value of this int
     * @param isTransient <code>true</code> if this int is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void write(int value, boolean isTransient, Field field)
	throws IOException {
	write(V_INT, Integer.toString(value, 10), isTransient, field);
    }
    
    /**
     * Write a long with a specified name.
     *
     * @param name the name of this long. May be null.
     * @param value the value of this long
     * @param isTransient <code>true</code> if this long is transient.
     * @exception IOException If an I/O error occurs
     */
    public void write(long value, boolean isTransient, Field field)
	throws IOException {
	write(V_LONG, Long.toString(value, 10), isTransient, field);
    }
    
    /**
     * Write a float with a specified name.
      *
     * @param name the name of this float. May be null.
     * @param value the value of this float
     * @param isTransient <code>true</code> if this float is transient.
     * @exception IOException If an I/O error occurs
    */    
    public void write(float value, boolean isTransient, Field field)
	throws IOException {
	write(V_FLOAT, Float.toString(value), isTransient, field);
    }
    
    /**
     * Write a double with a specified name.
     *
     * @param name the name of this double. May be null.
     * @param value the value of this double
     * @param isTransient <code>true</code> if this double is transient.
     * @exception IOException If an I/O error occurs
     */    
    public void write(double value, boolean isTransient, Field field)
	throws IOException {
	write(V_DOUBLE, Double.toString(value), isTransient, field);
    }

}
