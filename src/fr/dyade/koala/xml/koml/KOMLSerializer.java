/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * KOMLSerializer.java
 * $Id$
 */
package fr.dyade.koala.xml.koml;

import java.io.EOFException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.OutputStream;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLConnection;

import java.util.Enumeration;

import fr.dyade.koala.util.TaskCounter;
import fr.dyade.koala.util.TaskCounterInputStream;
import fr.dyade.koala.util.ByteInputOutputStream;
import fr.dyade.koala.serialization.ObjectOutputHandler;
import fr.dyade.koala.serialization.GeneratorInputStream;
import fr.dyade.koala.serialization.ClassDescription;

/**
 * This class serialize some java objects to a KOML document.
 *
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
public class KOMLSerializer 
        implements fr.dyade.koala.serialization.Serializer, TaskCounter {

    private KObjectOutputStream   ostream;
    private Serializer            out;
    private String                tmpFileName;
    private TaskCounter           counter;
    private boolean               buffered;
    private ByteInputOutputStream bytes;
    
    /**
     * Creates a new KOMLSerializer with an output stream.
     *
     * @param out the output stream
     * @param buffered If true, the KOMLDeserializer uses a temporary file (to
     *                 prevent memory crash) instead of an array of bytes.
     * @exception IOException           an I/O error occurs.
     */
    public KOMLSerializer(OutputStream out, boolean buffered) 
	    throws IOException {
	this.buffered = buffered;
	initialize(out);
    }

    private void initialize(OutputStream out) 
	    throws IOException {
	OutputStream os = null;
	if (buffered) {
	    int i = (int) System.currentTimeMillis();
	    if (i < 0) {
		i = - i;
	    }
	    tmpFileName = Integer.toString(i, 10);
	    os = new FileOutputStream(tmpFileName);
	} else {
	    bytes = new ByteInputOutputStream(1024);
	    os = bytes;
	}
	ostream = new KObjectOutputStream(os);
	
	this.out = new Serializer(out);
    }

    /**
     * Creates a new KOMLSerializer with a source.
     * The source can be a File, an URL, a String, etc.
     *
     * @param o  the source
     * @param buffered If true, the KOMLDeserializer uses a temporary file (to
     *                 prevent memory crash) instead of an array of bytes.
     * @exception IOException           an I/O error occurs.
     */
    public KOMLSerializer(Object o, boolean buffered)
	throws IOException {
	this.buffered = buffered;

	if (o instanceof OutputStream) {
	    initialize((OutputStream) o);
	} else if (o instanceof File) {
	    initialize(new FileOutputStream((File) o));
	} else if (o instanceof URL) {
	    URLConnection uc = ((URL) o).openConnection();
	    initialize(uc.getOutputStream());
	} else if (o instanceof URLConnection) {
	    initialize(((URLConnection) o).getOutputStream());
	} else if (o instanceof String) {
	    try {
		URL url = new URL(o.toString());
		URLConnection uc = url.openConnection(); 
		initialize(uc.getOutputStream());
	    } catch (MalformedURLException ex) {
		initialize(new FileOutputStream(o.toString()));
	    } 	    
	} else {
	    throw new IOException("unsupported source " + o.getClass());
	}
    }

    /**
     * Adds an object to the serialized output.
     *
     * @param obj the object to be serialized.
     * @exception IllegalStateException invalid call to this function.
     * @exception IOException           an I/O error occurs.
     */    
    public void addObject(Object obj) 
	    throws IOException {
	if (ostream == null) {
	    throw new IllegalStateException("Invalid call");
	}
	ostream.writeObject(obj);	
    }

    /**
     * Close the serializer and releases any system resources.
     *
     * @exception IOException an I/O error occurs.
     * @exception ClassNotFoundException Class of a serialized object cannot be found. 
     */    
    public void close() throws ClassNotFoundException, IOException {
	File f = null;
	TaskCounterInputStream fi = null;

	ostream.close();
	
	if (buffered) {
	    f = new File(tmpFileName);
	    fi = new TaskCounterInputStream(new FileInputStream(f), f.length());
	} else {
	    fi = new TaskCounterInputStream(bytes.getInputStream(), 
					    bytes.getSize()); 
	}

	counter = fi;

	Enumeration classes = ostream.getAllClassDescription();

        GeneratorInputStream in = new GeneratorInputStream(fi);
	try {
	    out.writeStartDocument();
	    out.writeStartClasses();

	    while (classes.hasMoreElements()) {
		out._writeClassDescription((ClassDescription) 
					   classes.nextElement());
	    }
	    out.writeEndClasses();
	    in.setObjectOuputHandler(out);
	    
	    try {
		while (true) {
		    in.readObject();
		}
	    } catch (EOFException e) {
		// ignore
	    }
	    out.writeEndDocument();
	} finally {
	    if (buffered) {
		f.delete();
	    }
	    out.close();
	    in.close(); // close fi too
	}
    }

    /**
     * Called by the garbage collector and call the close() method.
     */    
    protected void finalize() {
	try {
	    close();
	} catch (Exception e) {} // ignore
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
