/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * KOMLConstants.java
 * $Id$
 */
package fr.dyade.koala.xml.koml;

/**
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
public class KOMLConstants {

    //    public static final String VERSION = "30, Jun 1998";
//    public static final String KOML_DTD
//	= "http://www.inria.fr/koala/XML/koml12.dtd";
    public static String KOML_DTD = "http://localhost/pll2/koml12.dtd";
//     backward compatibility with an old version ..
    public static final String OLD_VERSION = "24, September 1998";
    public static final int MAJOR_VERSION = 1;
    public static final int MINOR_VERSION = 21;

    public static String KOML   = "koml";
    public static final String OBJECT = "object";
    public static final String OBJECT_CLASS = "object-class";
    public static final String CLASSES  = "classes";
    public static final String CLASS  = "class";
    public static final String ARRAY  = "array";
    public static final String NULL   = "null";
    public static final String REFERENCE = "reference";
    public static final String THIS   = "this";
    public static final String SUPER  = "super";
    public static final String FIELD  = "field";
    public static final String ROW    = "row";
    public static final String VALUE  = "value";

    // attributes names
    public static final String A_VERSION = "version";
    public static final String A_CLASS   = "class";
    public static final String A_TYPE    = "type";
    public static final String A_NAME    = "name";
    public static final String A_UID     = "uid";
    public static final String A_ID      = "id";
    public static final String A_REF     = "ref";
    public static final String A_LENGTH  = "length";
    public static final String A_SIZE    = "size";
    public static final String A_OBJECT  = "object";
    public static final String A_SUPER   = "super";
    public static final String A_WRITEMETHOD = "writemethod";
    public static final String A_IMPLEMENTS  = "implements";
    public static final String A_TRANSIENT   = "transient";

    // values
    public static final String V_BYTE    = "byte";
    public static final String V_SHORT   = "short";
    public static final String V_INT     = "int";
    public static final String V_LONG    = "long";
    public static final String V_FLOAT   = "float";
    public static final String V_DOUBLE  = "double";
    public static final String V_CHAR    = "char";
    public static final String V_BOOLEAN = "boolean";
    public static final String V_STRING  = "java.lang.String";
    public static final String V_TRUE           = "true";
    public static final String V_FALSE          = "false";
    public static final String V_SERIALIZABLE   = "serializable";
    public static final String V_EXTERNALIZABLE = "externalizable";

    /* HASH CODES */

    public static final int H_KOML      = KOML.hashCode();
    public static final int H_OBJECT    = OBJECT.hashCode();
    public static final int H_OBJECT_CLASS = OBJECT_CLASS.hashCode();
    public static final int H_CLASSES   = CLASSES.hashCode();
    public static final int H_CLASS     = CLASS.hashCode();
    public static final int H_ARRAY     = ARRAY.hashCode();
    public static final int H_NULL      = NULL.hashCode();
    public static final int H_REFERENCE = REFERENCE.hashCode();
    public static final int H_THIS      = THIS.hashCode();
    public static final int H_SUPER     = SUPER.hashCode();
    public static final int H_FIELD     = FIELD.hashCode();
    public static final int H_ROW       = ROW.hashCode();
    public static final int H_VALUE     = VALUE.hashCode();

    // primitive type
    public static final int H_V_BYTE    = V_BYTE.hashCode();
    public static final int H_V_SHORT   = V_SHORT.hashCode();
    public static final int H_V_INT     = V_INT.hashCode();
    public static final int H_V_LONG    = V_LONG.hashCode();
    public static final int H_V_FLOAT   = V_FLOAT.hashCode();
    public static final int H_V_DOUBLE  = V_DOUBLE.hashCode();
    public static final int H_V_CHAR    = V_CHAR.hashCode();
    public static final int H_V_BOOLEAN = V_BOOLEAN.hashCode();
    public static final int H_V_STRING  = V_STRING.hashCode();

    public static final int H_V_TRUE           = V_TRUE.hashCode();
    public static final int H_V_FALSE          = V_FALSE.hashCode();
    public static final int H_V_SERIALIZABLE   = V_SERIALIZABLE.hashCode();
    public static final int H_V_EXTERNALIZABLE = V_EXTERNALIZABLE.hashCode();

}
