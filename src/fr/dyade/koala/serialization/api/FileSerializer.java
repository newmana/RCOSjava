/* Copyright (c) 1998 by Groupe Bull. All Rights Reserved */
/* $Id$ */
/* Author: Thierry-Kormann@sophia.inria.fr  */

package fr.dyade.koala.serialization.api;

import java.io.IOException;
import fr.dyade.koala.serialization.*;


/**
 * Enables the XML serialization of the <code>java.util.Date</code> class.
 *
 * @author Thierry.Kormann@sophia.inria.fr 
 */
public class FileSerializer {

    public static void readObject(GeneratorInputStream s) 
	    throws ClassNotFoundException, IOException {
	s.defaultReadObject();
	s.readChar();
    }

}
