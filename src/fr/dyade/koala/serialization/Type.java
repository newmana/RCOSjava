/*
 * Types.java
 * $Id$
 */
package fr.dyade.koala.serialization;

/**
 *  Defines all types available for a field.  Uses the
 *  <code>equals(Object)</code> method to compare two types.
 *
 * @version $Revision$
 * @author Philippe Le Hégaret 
 * @see TypeFactory
 */
public class Type {
    /* prim_typecode */

    /** the byte type  */    
    public final static int BYTE    = 'B';

    /** the char type  */    
    public final static int CHAR    = 'C';

    /** the double type  */    
    public final static int DOUBLE  = 'D';

    /** the int type  */    
    public final static int FLOAT   = 'F';

    /** the int type  */    
    public final static int INT     = 'I';

    /** the long type  */    
    public final static int LONG    = 'J';

    /** the short type  */    
    public final static int SHORT   = 'S';

    /** the boolean type  */    
    public final static int BOOLEAN = 'Z';

    /* obj_typecode */

    /** the object type  */    
    public final static int OBJECT =       'L';
    
    /** the string type  */    
    public final static int STRING =       0x74;

    /** the array type  */    
    public final static int ARRAY =        '[';

    int type;

    String _name;

    Type _elementType;

    Type(int type) {
	this.type = type;
    }

    Type(String name) {
	this.type = OBJECT;
	_name = name;
    }

    Type(Type elementType) {
	this.type = ARRAY;
	_elementType = elementType;
    }

    /**
     * Returns the underlying type.
     * 
     * <pre>createObject("Short").equals(createObject("Boolean")) == false</pre>
     * but
     * <pre>createObject("Short").getTypeDefinition() == createObject("Boolean").getTypeDefinition()</pre>
     *
     * @return the underlying type
     */    
    public int getTypeDefinition() {
	return type;
    }

    /**
     * Returns the name of the object if the current type is an object
     *
     * @return    the name of the object
     * @exception Exception the current is not an object
     */    
    public String getName() throws Exception {
	throw new Exception("Illegal invocation");
    }

    /**
     * Returns the element's type if the current type is an array
     *
     * @return    the element's type
     * @exception Exception the current is not an array
     */    
    public Type getElementType() throws Exception {
	throw new Exception("Illegal invocation");
    }

    /**
     * Returns true if two types are equals
     */    
    public boolean equals(Type other) {
	return (type == other.type);
    }

    /**
     * Returns true if this type is the byte type
     */
    public boolean isByte() {
	return (type == BYTE);
    }

    /**
     * Returns true if this type is the short type
     */
    public boolean isShort() {
	return (type == SHORT);
    }

    /**
     * Returns true if this type is the int type
     */
    public boolean isInt() {
	return (type == INT);
    }

    /**
     * Returns true if this type is the long type
     */
    public boolean isLong() {
	return (type == LONG);
    }

    /**
     * Returns true if this type is the float type
     */
    public boolean isFloat() {
	return (type == FLOAT);
    }

    /**
     * Returns true if this type is the double type
     */
    public boolean isDouble() {
	return (type == DOUBLE);
    }

    /**
     * Returns true if this type is the boolean type
     */
    public boolean isBoolean() {
	return (type == BOOLEAN);
    }

    /**
     * Returns true if this type is the object type
     */
    public boolean isObject() {
	return (type == OBJECT);
    }

    /**
     * Returns true if this type is the string type
     */
    public boolean isString() {
	return (type == STRING);
    }

    /**
     * Returns true if this type is the array type
     */
    public boolean isArray() {
	return (type == ARRAY);
    }

    /**
     * Returns a string internal representation of this object.
     */
    String toStringInternal() {
	switch (type) {
	case Type.BYTE:
	    return "B";
	case Type.CHAR:
	    return "C";
	case Type.DOUBLE:
	    return "D";
	case Type.FLOAT:
	    return "F";
	case Type.INT:
	    return "I";
	case Type.LONG:
	    return "J";
	case Type.SHORT:
	    return "S";
	case Type.BOOLEAN:
	    return "Z";
	case Type.OBJECT:
	    return "L" + _name.replace('.', '/') + ";";
	case Type.STRING:
	    return "Ljava/lang/String;";
	case Type.ARRAY:
	    return  "[" + _elementType.toStringInternal();
	default:
	    throw new IllegalStateException("The type is unknown!");
	}       
    }

    /**
     * Returns a string representation of this object.
     * <pre>
     *  java.lang.String
     *  java.lang.Object[]
     * </pre>
     */
    public String toString() {
	switch (type) {
	case Type.BYTE:
	    return "byte";
	case Type.CHAR:
	    return "char";
	case Type.DOUBLE:
	    return "double";
	case Type.FLOAT:
	    return "float";
	case Type.INT:
	    return "int";
	case Type.LONG:
	    return "long";
	case Type.SHORT:
	    return "short";
	case Type.BOOLEAN:
	    return "boolean";
	case Type.OBJECT:
	    return _name;
	case Type.STRING:
	    return "java.lang.String";
	case Type.ARRAY:
	    return _elementType + "[]";
	default:
	    throw new IllegalStateException("The type is unknown!");
	}       
    }    
}
