/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * BlockDataException.java
 * $Id$
 */
package fr.dyade.koala.serialization;

import java.io.IOException;

/**
 * Unexpected data appeared in an ObjectInputStream trying to read
 * an Object.
 * This exception occurs when the stream contains primitive data
 * instead of the object expected by readObject.
 * The eof flag in the exception is true to indicate that no more
 * primitive data is available.
 * The count field contains the number of bytes available to read.
 *
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
public class BlockDataException extends IOException {

    /**
     * True if there is no more data in the buffered part of the stream.
     */
    public boolean eof;

    /**
     * The number of bytes of primitive data available to be read
     * in the current buffer.
     */
    public int     length;

    /**
     * Creates a new BlockDataException with no more data to read
     */
    BlockDataException() {
	this.eof = true;
    }

    /**
     * Creates a new BlockDataException
     */
    BlockDataException(int length) {
       this.length = length;
    }
    
    public String getMessage() {
	if (eof) {
	    return "End of block data";
	} else {
	    return Integer.toString(length, 10);
	}
    }
    
}
