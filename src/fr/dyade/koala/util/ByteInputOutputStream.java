/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * ByteInputOutputStream.java
 * $Id$
 */
package fr.dyade.koala.util;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * This class is an input/output stream.
 *
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
public class ByteInputOutputStream extends ByteArrayOutputStream {

    /**
     * Creates a new byte array output stream. The buffer capacity is initially
     * 32 bytes, though its size increases if necessary.  
     */
    public ByteInputOutputStream() {
        super();
    }
    

    /**
     * Creates a new byte array output stream, with a buffer capacity of the
     * specified size, in bytes.  
     *
     * @param size the initial size. 
     */
    public ByteInputOutputStream(int size) {
	   super(size);
    }
	   

    public byte[] getInternalArrayByte() throws IOException {
	this.flush();
	return buf;
    }

    public InputStream getInputStream() throws IOException {
	this.flush();
	return new ByteArrayInputStream(buf, 0, count);
    }

    public int getSize() {
	return count;
    }
}
