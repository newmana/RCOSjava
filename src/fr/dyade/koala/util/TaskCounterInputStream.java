/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * FileCounterInputStream.java
 * $Id$
 */
package fr.dyade.koala.util;

import java.io.InputStream;
import java.io.IOException;

/**
 * This is a task counter for an input stream. Marks are not supported by the
 * task for the moment.
 *
 * @version $Revision$
 * @author Philippe Le Hégaret 
 */
public class TaskCounterInputStream extends InputStream 
        implements TaskCounter {

    /**
     * The underlying input stream
     */    
    private InputStream input;

    /**
     * The total length of the file
     */    
    private long total;
    private long current;
    
    /**
     * Creates an input file stream to read from the specified 
     * <code>File</code> object. 
     *
     * @param      input the input stream
     * @param      total the length of the input stream
     */
    public TaskCounterInputStream(InputStream input, long total) {
	this.input = input;
	this.total = total;
    }

    /**
     * This method returns the total length of the task.
     */    
    public long getMaximumValue() {
	return total;
    }

    /**
     * This method returns the current position of the task.
     */    
    public long getValue() {
	return current;
    }

    /**
     * Reads a byte of data from this input stream. This method blocks 
     * if no input is yet available. 
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             file is reached.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public int read() throws IOException {
	current++;
	return input.read();
    }
    
    
    /**
     * Reads up to <code>b.length</code> bytes of data from this input 
     * stream into an array of bytes. This method blocks until some input 
     * is available. 
     *
     * @param      b   the buffer into which the data is read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the file has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public int read(byte b[]) throws IOException {
	int result = input.read(b);
	current += result;
	return result;
    }

    /**
     * Reads up to <code>len</code> bytes of data from this input stream 
     * into an array of bytes. This method blocks until some input is 
     * available. 
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the file has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public int read(byte b[], int off, int len) throws IOException {
	int result = input.read(b, off, len);
	current += result;
	return result;
    }

    /**
     * Skips over and discards <code>n</code> bytes of data from the 
     * input stream. The <code>skip</code> method may, for a variety of 
     * reasons, end up skipping over some smaller number of bytes, 
     * possibly <code>0</code>. The actual number of bytes skipped is returned.
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the actual number of bytes skipped.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public long skip(long n) throws IOException {
	long result = input.skip(n);
	current += result;
	return result;
    }

   /**
     * Returns the number of bytes that can be read from this input 
     * stream without blocking. The available method of 
     * <code>InputStream</code> returns <code>0</code>. This method 
     * <B>should</B> be overridden by subclasses. 
     *
     * @return     the number of bytes that can be read from this input stream
     *             without blocking.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public int available() throws IOException {
        return 0;
    }

    /**
     * Closes this input stream and releases any system resources 
     * associated with the stream. 
     * <p>
     * The <code>close</code> method of <code>InputStream</code> does nothing.
     *
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public void close() throws IOException {
	input.close();
    }

    /**
     * Marks the current position in this input stream. A subsequent 
     * call to the <code>reset</code> method repositions this stream at 
     * the last marked position so that subsequent reads re-read the same 
     * bytes. 
     * <p>
     * The <code>readlimit</code> arguments tells this input stream to 
     * allow that many bytes to be read before the mark position gets 
     * invalidated. 
     * <p>
     * The <code>mark</code> method of <code>InputStream</code> does nothing.
     *
     * @param   readlimit   the maximum limit of bytes that can be read before
     *                      the mark position becomes invalid.
     * @see     java.io.InputStream#reset()
     * @since   JDK1.0
     */
    public synchronized void mark(int readlimit) {
	input.mark(readlimit);
    }

    /**
     * Repositions this stream to the position at the time the 
     * <code>mark</code> method was last called on this input stream. 
     * <p>
     * The <code>reset</code> method of <code>InputStream</code> throws 
     * an <code>IOException</code>, because input streams, by default, do 
     * not support <code>mark</code> and <code>reset</code>.
     * <p>
     * Stream marks are intended to be used in
     * situations where you need to read ahead a little to see what's in
     * the stream. Often this is most easily done by invoking some
     * general parser. If the stream is of the type handled by the
     * parser, it just chugs along happily. If the stream is not of
     * that type, the parser should toss an exception when it fails,
     * which, if it happens within readlimit bytes, allows the outer
     * code to reset the stream and try another parser.
     *
     * @exception  IOException  if this stream has not been marked or if the
     *               mark has been invalidated.
     * @see     java.io.InputStream#mark(int)
     * @see     java.io.IOException
     * @since   JDK1.0
     */
    public synchronized void reset() throws IOException {
	input.reset();
    }
    
    /**
     * Tests if this input stream supports the <code>mark</code> 
     * and <code>reset</code> methods. The <code>markSupported</code> 
     * method of <code>InputStream</code> returns <code>false</code>. 
     *
     * @return  <code>true</code> if this true type supports the mark and reset
     *          method; <code>false</code> otherwise.
     * @see     java.io.InputStream#mark(int)
     * @see     java.io.InputStream#reset()
     * @since   JDK1.0
     */
    public boolean markSupported() {
        return input.markSupported();
    }
}
