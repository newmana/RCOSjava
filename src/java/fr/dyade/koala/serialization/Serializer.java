/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * Serializer.java
 * $Id$
 */
package fr.dyade.koala.serialization;

import java.io.IOException;

/**
 * This class dfines a standart serializer. All serializer sould implement this
 * interface.
 *
 * @version $Revision$
 * @author Philippe Le Hégaret 
 */
public interface Serializer {

    /**
     * Adds an object to the serialized output.
     *
     * @param obj the object to be serialized.
     * @exception IllegalStateException invalid call to this function.
     * @exception IOException           an I/O error occurs.
     * @exception ClassNotFoundException Class of a serialized object cannot be found.
     */    
    void addObject(Object obj) throws ClassNotFoundException, IOException;

    /**
     * Close the serializer.
     *
     * @exception IOException an I/O error occurs.
     * @exception IOException           an I/O error occurs.
     * @exception ClassNotFoundException .
     */    
    void close() throws ClassNotFoundException, IOException;
}
