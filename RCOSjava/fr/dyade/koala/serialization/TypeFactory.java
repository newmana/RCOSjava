/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * TypeFactory.java
 * $Id$
 */
package fr.dyade.koala.serialization;

/**
 * This class creates all types for you.
 *
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
public final class TypeFactory {

    private final static Type _byte    = new Type(Type.BYTE);
    private final static Type _char    = new Type(Type.CHAR);
    private final static Type _double  = new Type(Type.DOUBLE);
    private final static Type _float   = new Type(Type.FLOAT);
    private final static Type _int     = new Type(Type.INT);
    private final static Type _long    = new Type(Type.LONG);
    private final static Type _short   = new Type(Type.SHORT);
    private final static Type _boolean = new Type(Type.BOOLEAN);
    private final static Type _string  = new Type(Type.STRING) {
		public String getName() throws Exception {
		    return "java.lang.String";
		}
	};

    /**
     * Creates the byte type.
     * Ensures <code>createByte().equals(createByte()) == true</code>
     *
     * @return the byte type
     */    
    public static Type createByte() {
	return _byte;
    }

    /**
     * Creates the short type.
     * Ensures <code>createShort().equals(createShort()) == true</code>
     *
     * @return the short type
     */    
    public static Type createShort() {
	return _short;
    }

    /**
     * Creates the int type.
     * Ensures <code>createInt().equals(createInt()) == true</code>
     *
     * @return the int type
     */    
    public static Type createInt() {
	return _int;
    }

    /**
     * Creates the float type.
     * Ensures <code>createFloat().equals(createFloat()) == true</code>
     *
     * @return the float type
     */    
    public static Type createFloat() {
	return _float;
    }

    /**
     * Creates the long type.
     * Ensures <code>createLong().equals(createLong()) == true</code>
     *
     * @return the long type
     */    
    public static Type createLong() {
	return _long;
    }

    /**
     * Creates the double type.
     * Ensures <code>createDouble().equals(createDouble()) == true</code>
     *
     * @return the double type
     */    
    public static Type createDouble() {
	return _double;
    }

    /**
     * Creates the char type.
     * Ensures <code>createChar().equals(createChar()) == true</code>
     *
     * @return the char type
     */    
    public static Type createChar() {
	return _char;
    }

    /**
     * Creates the string type.
     * Ensures <code>createString().equals(createString()) == true</code>
     *
     * @return the string type
     */    
    public static Type createString() {
	return _string;
    }

    /**
     * Creates the boolean type.
     * Ensures <code>createBoolean().equals(createBoolean()) == true</code>
     *
     * @return the boolean type
     */    
    public static Type createBoolean() {
	return _boolean;
    }

    /**
     * Creates the object type.
     * Ensures
     * <code>createObject("Short").equals(createObject("Short")) == true</code>
     *
     * @return the object type
     */    
    public static Type createObject(String name) {
	if (name.equals("java.lang.String")) {
	    return TypeFactory.createString();
	} else {
	    return new Type(name) {
		public String getName() throws Exception {
		    return _name;
		}
		public boolean compare(Type other) {
		    return ((other.type == Type.INT)
			    && _name.equals(other._name));
		}
	    };
	}
    }

    /**
     * Creates the array type.
     * Ensures
     * <code>createArray(elementType).equals(createObject(elementType)) == true</code>
     *
     * @param elementType the type of elements
     * @return the array type
     */    
    public static Type createArray(Type elementType) {
	return new Type(elementType) {
	    public Type getElementType() throws Exception {
		return _elementType;
	    }
	    public boolean compare(Type other) {
		return ((other.type == Type.ARRAY)
			&& _elementType.equals(other._elementType));
	    }
	};
    }

    /**
     * Creates an internal type with a given string
     */    
    static Type createTypeInternal(String name) throws Exception {
	byte code = (byte) name.charAt(0);
	String rest;

        switch (code) {
        case ObjectStreamConstants.BYTE_TYPE:
            return TypeFactory.createByte();
        case ObjectStreamConstants.CHAR_TYPE:
            return TypeFactory.createChar();
        case ObjectStreamConstants.DOUBLE_TYPE:
            return TypeFactory.createDouble();
        case ObjectStreamConstants.FLOAT_TYPE:
            return TypeFactory.createFloat();
        case ObjectStreamConstants.INT_TYPE:
            return TypeFactory.createInt();
        case ObjectStreamConstants.LONG_TYPE:
            return TypeFactory.createLong();
        case ObjectStreamConstants.SHORT_TYPE:
            return TypeFactory.createShort();
        case ObjectStreamConstants.BOOLEAN_TYPE:
            return TypeFactory.createBoolean();
        case ObjectStreamConstants.ARRAY_TYPE:
	    rest = name.substring(1);
	    return TypeFactory.createArray(TypeFactory.createTypeInternal(rest));
        case ObjectStreamConstants.OBJECT_TYPE:
	    rest = name.substring(1, name.indexOf(';'));
	    return TypeFactory.createObject(rest);
	default:
	    throw new Exception("Invalid type " + code);
	}
    }

    /**
     * Creates a type with a given string
     *
     * <pre>
     *  java.lang.String
     *  java.lang.Object[]
     * </pre>
     *
     * @param stype the string representation of this type.
     * @return the new type.
     */    
    public static Type createType(String stype) {
	int last = stype.lastIndexOf('[');
	if (last != -1) {
	    return TypeFactory.createArray(createType(stype.substring(0, last)));
	} else {
	    if (stype.equals("byte")) {
		return TypeFactory.createByte();
	    } else if (stype.equals("short")) {
		return TypeFactory.createShort();
	    } else if (stype.equals("int")) {
		return TypeFactory.createInt();
	    } else if (stype.equals("long")) {
		return TypeFactory.createLong();
	    } else if (stype.equals("float")) {
		return TypeFactory.createFloat();
	    } else if (stype.equals("double")) {
		return TypeFactory.createDouble();
	    } else if (stype.equals("boolean")) {
		return TypeFactory.createBoolean();
	    } else if (stype.equals("char")) {
		return TypeFactory.createChar();
	    } else {
		return TypeFactory.createObject(stype);
	    }
	}
    }
}
