/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * ObjectOutputHandler.java
 * $Id$
 */
package fr.dyade.koala.serialization;

import java.io.IOException;

/**
 * This handler let you to control output of ojects.
 * For example, if you want to convert an ObjectInputStream to an XML document,
 * you can use the generator input stream and control your XML document with
 * this handler.
 *
 * @version $Revision$
 * @author Philippe Le Hégaret 
 */
public interface ObjectOutputHandler {

    /**
     * Notify the start of the serialization.
     *
     * @exception IOException If an I/O error occurs
     */    
    void writeStartDocument() throws IOException;
    
    /**
     * Notify the end of the serialization.
     *
     * @exception IOException If an I/O error occurs
     */    
    void writeEndDocument() throws IOException;

    /**
     * Write the start of an object.
     *
     * @param id the unique of this object.
     * @param _class the class description of this object.
     * @param isTransient <code>true</code> if this boolean is transient.
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void writeStartObject(int id, 
			  ClassDescription _class, 
			  boolean isTransien,
			  Field field)
	throws IOException;

    /**
     * Write the end of an object.
     *
     * @param _super the class description of the super instance.
     * @param _class the class description of this object.
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void writeEndObject(int id, ClassDescription _class, Field field)
	throws IOException;

    /**
     * Write the start of a class object.
     *
     * @param id the unique of this object.
     * @param _class the class description of this object.
     * @param isTransient <code>true</code> if this boolean is transient.
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void writeStartObjectClass(int id, 
			       ClassDescription _class, 
			       boolean isTransient,
			       Field field)
	throws IOException;

    /**
     * Write the end of a class object.
     *
     * @param _super the class description of the super instance.
     * @param _class the class description of this object.
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void writeEndObjectClass(int id, 
			     ClassDescription _class, 
			     Field field) 
	throws IOException;

    /**
     * Write the start of a super instance.
     *
     * @param _super the class description of the super instance.
     * @exception IOException If an I/O error occurs
     */    
    void writeStartSuper(ClassDescription _super) throws IOException;

    /**
     * Write the end of the super instance.
     *
     * @exception IOException If an I/O error occurs
     */    
    void writeEndSuper(ClassDescription _super) throws IOException;

    /**
     * Write the start of an array.
     *
     * @param id the unique of this array.
     * @param _class the class description of this array.
     * @param size the size of this array.
     * @param isTransient <code>true</code> if this boolean is transient.
     * @param field The field of this array, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void writeStartArray(int id, ClassDescription _class, 
			 int size, boolean isTransient,
			 Field field)
	throws IOException;

    /**
     * Write the end of an array.
     *
     * @param id the unique id of this array.
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void writeEndArray(int id, ClassDescription _class, Field field)
	throws IOException;

    /**
     * Write a null reference to an object or an array.
     *
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void writeNullReference(Field field) throws IOException;

    /**
     * Write a reference to an object or an array.
     *
     * @param id the id reference.
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void writeReference(int id, Field field) throws IOException;

    /**
     * Write a new class description. The super class of this one can be not
     * set yet.
     *
     * @param _class the new class description
     * @exception IOException If an I/O error occurs 
     */
    void writeClassDescription(ClassDescription _class) throws IOException;

    /**
     * Write a string.
     *
     * @param value the value of this string
     * @param isTransient <code>true</code> if this string is transient.
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void write(String value, boolean isTransient, Field field)
	throws IOException;
    
    /**
     * Write a boolean.
     *
     * @param value the value of this boolean
     * @param isTransient <code>true</code> if this boolean is transient.
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void write(boolean value, boolean isTransient, Field field)
	throws IOException;
    
    /**
     * Write a byte.
     *
     * @param value the value of this byte
     * @param isTransient <code>true</code> if this byte is transient.
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void write(byte value, boolean isTransient, Field field)
	throws IOException;
    
    /**
     * Write a char.
     *
     * @param value the value of this char
     * @param isTransient <code>true</code> if this char is transient.
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void write(char value, boolean isTransient, Field field)
	throws IOException;
    
    /**
     * Write a short.
     *
     * @param value the value of this short
     * @param isTransient <code>true</code> if this short is transient.
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void write(short value, boolean isTransient, Field field)
	throws IOException;
    
    /**
     * Write an int.
     *
     * @param value the value of this int
     * @param isTransient <code>true</code> if this int is transient.
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void write(int value, boolean isTransient, Field field)
	throws IOException;
    
    /**
     * Write a long.
     *
     * @param value the value of this long
     * @param isTransient <code>true</code> if this long is transient.
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void write(long value, boolean isTransient, Field field)
	throws IOException;
    
    /**
     * Write a float.
     *
     * @param value the value of this float
     * @param isTransient <code>true</code> if this float is transient.
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void write(float value, boolean isTransient, Field field)
	throws IOException;
    
    /**
     * Write a double.
     *
     * @param value the value of this double
     * @param isTransient <code>true</code> if this double is transient.
     * @param field The field of this object, null otherwise
     * @exception IOException If an I/O error occurs
     */    
    void write(double value, boolean isTransient, Field field)
	throws IOException;

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
    void writeRow(byte[] data, int offset, int length) throws IOException;

    /**
     * Close this output stream and releases any system ressources associated 
     * with this stream.
     * @exception IOException If an I/O error occurs 
     */    
    void close() throws IOException;

    /**
     * Flushes the object output handler
     * @exception IOException If an I/O error occurs 
     */    
    void flush() throws IOException;
}
