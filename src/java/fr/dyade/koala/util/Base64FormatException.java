/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * Base64FormatException.java
 * $Id$
 */
package fr.dyade.koala.util;

import java.io.IOException;

/**
 * @version $Revision$
 * @author  Philippe Le Hegaret
 */
public class Base64FormatException extends IOException {

    /**
     * Creates a new Base64FormatException
     */
    public Base64FormatException() {
	super();
    }
    
    /**
     * Creates a new Base64FormatException
     */
    public Base64FormatException(String s) {
	super(s);
    }
}
