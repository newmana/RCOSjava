/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * ClassDescription.java
 * $Id$
 */
package fr.dyade.koala.serialization;

import java.util.Enumeration;
import java.lang.reflect.Method;

/**
 * A class description contains all informations about a class
 *
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
public class ClassDescription {
    int              id;
    int              type;
    String           name;
    Field[]          fields;

    // to optimize GeneratorInputStream
    int[]            fieldsQuick;
    Method           method;

    short            sizeField;
    Type             _type;
    ClassDescription superClass;
    byte[]           data;
    long             serial;
    byte             flag;
    
    
    /**
     * Create a new ClassDescription with a specified class name.
     * The class description can be an array. In that case, the method
     * isArray() returns true.
     *
     * @param name the full name of this class
     */
    public ClassDescription(String name) {
	type = ObjectStreamConstants.TC_CLASSDESC;
        this.name = name;
    }

    /**
     * Set the unique id of this class description
     *
     * @param id the new value for the id
     */
    void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the unique id
     *
     * @return the value of the id
     */
    int getId() {
        return id;
    }    
    
    /**
     * Returns the full name of this class
     *
     * @return the full name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of this class
     */
    public Type getType() {
	return _type;
    }

    /**
     * Set the type of this class.
     */    
    public void setType(Type type) { 
	this._type = type;
    }
    
    /**
     * Set all fields for this class
     *
     * @param fields All fields
     */
    void setFields(Field[] fields) {
        this.fields = fields;
	sizeField = (short) fields.length;
	fieldsQuick = new int[sizeField];

	for (int i = 0; i < sizeField; i++) {
	    fieldsQuick[i] = fields[i].getType().getTypeDefinition();
	}
    }

    /**
     * Adds the specified transient field to this instance.
     * @param t the transient field to add
     */    
    public void addField(Field t) {
	if (sizeField == 0) {
	    fields = new Field[5];
	} else if (sizeField == fields.length) {
	    Field[] old = fields;
	    fields = new Field[old.length * 2];
	    System.arraycopy(old, 0, fields, 0, sizeField);
	}
	fields[sizeField++] = t;
    }

    /**
     * Returns all fields of this class description
     */    
    public Field[] getFields() {
	short max = sizeField;

	if (fields == null) {
	    return null;
	} else if (sizeField != fields.length) {
	    Field[] old = fields;
	    fields = new Field[sizeField];
	    System.arraycopy(old, 0, fields, 0, sizeField);
	}
	return fields;
    }

    /**
     * Set the super class
     *
     * @param superClass the super class
     */
    public void setSuperClass(ClassDescription superClass) {
        this.superClass = superClass;
    }
    
    /**
     * Returns the super class.
     *
     * @return the super class, null otherwise
     */
    public ClassDescription getSuperClass() {
        return superClass;
    }
    
    /**
     * Set the serial version UID
     *
     * @param serial the serial version UID
     */
    public void setSerialVersionUID(long serial) {
        this.serial = serial;
    }

    /**
     * Returns the serial version UID of this class
     */
    public long getSerialVersionUID() {
        return serial;
    }

    /**
     * Set the attribute data
     *
     * @param data the new value for the attribute
     */
    public void setData(byte[] data) {
	if (fields != null) {
	    throw new IllegalStateException("A class is readable or not" 
					    + ", but not twice !");
	}
        this.data = data;
    }
    
    /**
     * Returns data for this class. If a class is not readable, all data
     * go here.
     *
     * @return all data
     */
    public byte[] getData() {
        return data;
    }
    
    /**
     * This method returns the string representation of the type
     *
     * @return the string representation of the type
     */
    public String toString() {
	return getName();
    }
    
    /**
     * Returns all fields for this class and his super class
     */    
    public Enumeration getAllFields() {
	throw new IllegalStateException("Eh gaillard ! t'as qu'a la " 
					+ "faire la fonction !");
    }

    /**
     * Set this class with a writeObject method
     */    
    public void setHasWriteMethod() {	
	flag |= ObjectStreamConstants.SC_WRITE_METHOD;
    }

    /**
     * This method returns true if this class overloaded the writeObject method
     *
     * @return true if this class overloads the writeObject method
     * @see java.io.Serializable
     */    
    public boolean hasWriteMethod() {
	return ((flag & ObjectStreamConstants.SC_WRITE_METHOD) != 0);
    }

    /**
     * This class extends java.io.Serializable
     */    
    public void setIsSerializable() {
	flag |= ObjectStreamConstants.SC_SERIALIZABLE;
    }
    
    /**
     * This method returns true if this class extends java.io.Serializable.
     *
     * @see java.io.Serializable
     */
    public boolean isSerializable() {
	return ((flag & ObjectStreamConstants.SC_SERIALIZABLE) != 0);
    }

    /**
     * This class extends java.io.Externalizable
     */    
    public void setIsExternalizable() {
	flag |= ObjectStreamConstants.SC_EXTERNALIZABLE;
    }
    
    /**
     * This method returns true if this class extends java.io.Externalizable.
     *
     * @see java.io.Externalizable
     */
    public boolean isExternalizable() {
	return ((flag & ObjectStreamConstants.SC_EXTERNALIZABLE) != 0);
    }

    /**
     * Useful method for the parser
     */    
    void setFlag(byte flag) {
	this.flag = flag;
    }

    /**
     * Useful method for the parser
     */    
    int getFlag() {
	return flag;
    }

    /**
     * Compares two class dscription for equality
     */    
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof ClassDescription)) {
	    return ((ClassDescription) obj).name.equals(name);
	}
	return false;
    }

    /**
     * Returns a hash code value for this class description.
     */    
    public int hashCode() {
	return name.hashCode();
    }
}
