/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * KOMLDeserializer.java
 * $Id$
 */
package fr.dyade.koala.xml.koml;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import fr.dyade.koala.util.TaskCounter;
import fr.dyade.koala.util.TaskCounterInputStream;
import fr.dyade.koala.util.ByteInputOutputStream;
import fr.dyade.koala.serialization.Deserializer;
import fr.dyade.koala.serialization.ObjectOutputHandler;
import fr.dyade.koala.serialization.GeneratorOutputStream;

/**
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
public class KOMLDeserializer implements Deserializer, TaskCounter {

    // the Java ObjectInputStream
    private ObjectInputStream istream;

    private File              tmpFile;

    private String            input;

    private boolean           buffered;

    // let people to follow the process
    private TaskCounterInputStream       counter;
    
    /**
     * Creates a new KOMLDeserializer
     *
     * @param in the input stream.
     * @param buffered If true, the KOMLDeserializer uses a temporary file (to
     *                 prevent memory crash) instead of an array of bytes.
     * @exception IOException an I/O error occurs.  
     */
    public KOMLDeserializer(InputStream in, boolean buffered)
	    throws IOException {
	this.buffered = buffered;
	initialize(in);
    }

    /**
     * Converts the Reader in into a Java ObjectInputStream
     */
    private void initialize(InputStream in) throws IOException {
	ByteInputOutputStream bytes = null;

	ObjectOutputHandler out  = null;
	if (buffered) {
	    // creates the temporary file with a random number
	    int i = (int) System.currentTimeMillis();
	    if (i < 0) {
		i = - i;
	    }
	    input = Integer.toString(i, 10);
	    out = new GeneratorOutputStream(new FileOutputStream(input)); 
	} else {
	    bytes = new ByteInputOutputStream(1024);
	    out = new GeneratorOutputStream(bytes); 
	}
	try {
	    KOMLParser parser = new KOMLParser();
	    parser.setObjectOuputHandler(out);
	    parser.read(in);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new IOException(e.toString());
	} finally {
	    out.close();
	    in.close();
	}

	if (buffered) {
	    tmpFile = new File(input);
	    counter = 
		new TaskCounterInputStream(new FileInputStream(tmpFile), 
					   tmpFile.length());
	} else {
	    counter = 
		new TaskCounterInputStream(bytes.getInputStream(), 
					   bytes.getSize());
	    bytes.close();
	}
    }
    
    /**
     * Creates a new KOMLDeserializer with a source.
     * The source can be a File, an URL, a String, etc.
     *
     * @param o  the source
     * @param buffered If true, the KOMLDeserializer uses a temporary file (to
     *                 prevent memory crash) instead of an array of bytes.
     * @exception IOException           an I/O error occurs.
     */
    public KOMLDeserializer(Object o, boolean buffered) throws IOException {
	this.buffered = buffered;
	if (o instanceof InputStream) {
	    initialize((InputStream) o);
	} else if (o instanceof File) {
	    initialize(new FileInputStream((File) o));
	} else if (o instanceof URL) {
	    URLConnection uc = ((URL) o).openConnection();
	    initialize(uc.getInputStream());
	} else if (o instanceof URLConnection) {
	    initialize(((URLConnection) o).getInputStream());
	} else if (o instanceof String) {
	    try {
		URL url = new URL(o.toString());
		URLConnection uc = url.openConnection(); 
		initialize(uc.getInputStream());
	    } catch (MalformedURLException ex) {
		initialize(new FileInputStream(o.toString()));
	    } 	    
	} else {
	    throw new IOException("unsupported source " + o.getClass());
	}
    }

    /**
     * Returns the Object input stream to use for the Java deserialization.
     * Overrides this function to change the object input stream.
     *
     * @exception IOException an I/O error occurs.
     */    
    protected ObjectInputStream getObjectInputStream(InputStream in) 
            throws IOException {
	return new ObjectInputStream(counter);
    }

    /**
     * Read an object.
     *
     * @exception IOException an I/O error occurs
     * @exception ClassNotFoundException .
     * @return an object or throws an EOFException
     */    
    public Object readObject() throws ClassNotFoundException, IOException {	
	if (istream == null) {
	    istream = getObjectInputStream(counter);
	}
	return istream.readObject();
    }

    /**
     * Close the deserializer and releases any system resources.
     *
     * @exception IOException an I/O error occurs.
     */    
    public void close() throws IOException {
	if (tmpFile != null) {
	    tmpFile.delete();
	}
	if (istream != null) {
	    istream.close();
	}
    }

    /**
     * Called by the garbage collector and call the close() method.
     */    
    protected void finalize() {
	try {
	    close();
	} catch (IOException e) {} // ignore
    }

    /**
     * This method returns the total length of the task.
     *
     * @return the length of the task, -1 otherwise
     */    
    public long getMaximumValue() {
	if (counter == null) {
	    return -1;
	}
	return counter.getMaximumValue();
    }

    /**
     * This method returns the current position of the task.
     */    
    public long getValue() {
	if (counter == null) {
	    return 0;
	}
	return counter.getValue();
    }

}
