/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * Field.java
 * $Id$
 */
package fr.dyade.koala.serialization;

/**
 * This class defines a field : his name and his type.
 *
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
public class Field {

    String           name;
    Type             type;

    /**
     * Creates a new Field.
     */
    protected Field() {       
    }
    
    /**
     * Creates a new Field for a primitive, an exception or a string.
     *
     * @param type the type of this field
     * @param name the name of this field
     * @see Types
     */
    public Field(Type type, String name) {
	this.type = type;
	this.name = name;
    }
    
    /**
     * Returns the name of this field.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of this field.
     *
     * @return the type of this field
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the string representation of the type.
     *
     * @return the string representation of the type
     */
    public String toString() {
	return type + " " + name;
    }
    
}
