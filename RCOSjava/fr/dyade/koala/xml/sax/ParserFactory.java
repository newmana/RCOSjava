/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * ParserFactory.java
 * $Id$
 */
package fr.dyade.koala.xml.sax;

import org.xml.sax.Parser;
import org.xml.sax.SAXException;

/**
 * This class helps to create a SAX Parser.
 *
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
public class ParserFactory extends org.xml.sax.helpers.ParserFactory {

    private final static String[] allParsers =
    {
	"com.sun.xml.parser.Parser",
	"com.microstar.xml.SAXDriver",
	"fr.dyade.koala.xml.sax.SAXParser",
	"com.sun.xml.parser.ValidatingParser",
	"com.ibm.xml.parser.SAXDriver",
	"com.jclark.xml.sax.Driver",
	"com.datachannel.xml.sax.SAXDriver",
	"com.megginson.sax.LarkDriver",
	"com.megginson.sax.MSXMLDriver"
    };
    
    /**
     * Don't create a new ParserFactory
     */
    private ParserFactory() {
       
    }
    

    /**
     * Creates a new SAX parser object using the system property
     * `org.xml.sax.parser'.
     * If no system property found, try to find a SAX Parser, here is the
     * search list :
     * <ol>
     *  <li>"com.sun.xml.parser.Parser"
     *  <li>"com.microstar.xml.SAXDriver"
     *  <li>"fr.dyade.koala.xml.sax.SAXParser"
     *  <li>"com.sun.xml.parser.ValidatingParser"
     *  <li>"com.ibm.xml.sax.Driver"
     *  <li>"com.jclark.xml.sax.Driver"
     *  <li>"com.datachannel.xml.sax.SAXDriver"
     *  <li>"com.megginson.sax.LarkDriver"
     *  <li>"com.megginson.sax.MSXMLDriver"
     * </ol>
     *
     * @exception java.lang.ClassNotFoundException The SAX parser
     *            class was not found (check your CLASSPATH).
     * @exception IllegalAccessException The SAX parser class was
     *            found, but you do not have permission to load
     *            it.
     * @exception InstantiationException The SAX parser class was
     *            found but could not be instantiated.
     * @exception java.lang.ClassCastException The SAX parser class
     *            was found and instantiated, but does not implement
     *            org.xml.sax.Parser.
     */
    public static Parser makeParser() 
  	    throws ClassNotFoundException, 
	           IllegalAccessException, 
 	           InstantiationException,
          	   ClassCastException {
	try {
	    return org.xml.sax.helpers.ParserFactory
		.makeParser();
	} catch (NullPointerException e) {
	    // ignore
	} catch (SecurityException e) {
	    // ignore
	}
	for (int i = 0; i < allParsers.length; i++) {
	    try {
		return org.xml.sax.helpers.ParserFactory
		    .makeParser(allParsers[i]);
	    } catch (ClassNotFoundException ex) {
		// ignore
	    }
	}
	throw new ClassNotFoundException("No SAX Parser found.");
    }

}
