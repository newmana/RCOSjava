/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * Deserializer.java
 * $Id$
 */
package fr.dyade.koala.serialization;

import java.io.IOException;

/**
 * This interface defines a standart deserializer. Any deserializer should
 * implements this interface.
 *
 * @version $Revision$
 * @author Philippe Le Hégaret 
 */
public interface Deserializer {

    /**
     * Read an object.
     *
     * @exception IOException an I/O error occurs
     * @exception ClassNotFoundException Class of a serialized object cannot be found.
     * @return an object or throws an EOFException at the end of file
     */    
    Object readObject() throws ClassNotFoundException, IOException;

    /**
     * Close the deserializer.
     *
     * @exception IOException an I/O error occurs.
     */    
    public void close() throws IOException;
}
