/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * KOMLException.java
 * $Id$
 */
package fr.dyade.koala.xml.koml;

import org.xml.sax.SAXException;

/**
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
class KOMLException extends SAXException {
    public KOMLException(String s) {
	super(s);
    }

    public KOMLException(String s, Exception e) {
	super(s, e);
    }
}
