/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * KObjectOutputStream.java
 * $Id$
 */
package fr.dyade.koala.xml.koml;

import java.io.IOException;
import java.io.EOFException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import fr.dyade.koala.serialization.GeneratorInputStream;
import fr.dyade.koala.serialization.ClassDescription;
import fr.dyade.koala.serialization.ObjectOutputHandler;
import fr.dyade.koala.serialization.Field;
import fr.dyade.koala.util.ByteInputOutputStream;

/**
 * This class is used to get all class description from an ObjectOutputStream. 
 *
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
class KObjectOutputStream extends ObjectOutputStream {
    
    Vector v = new Vector();
    
    /**
     * Creates a new KObjectOutputStream
     */
    KObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }
    
    /**
     * Overrides the ObjectOutputStream method. Get all class used by the
     * Java serialization.
     */    
    protected void annotateClass(Class cl) throws IOException {
	v.addElement(cl);
    }
    
    /**
     * Converts all class into class description.
     */
    Enumeration getAllClassDescription() 
	    throws IOException, ClassNotFoundException {
	InputStream input = null;
	ByteInputOutputStream tout = new ByteInputOutputStream();       
	ObjectOutputStream out = new ObjectOutputStream(tout);
	
	try {
	    for (int i = 0; i < v.size(); i++) {
		out.writeObject(v.elementAt(i));
	    }
	    out.flush();
	} finally {
	    out.close();
	}
	input = tout.getInputStream();
	
	Hashtable classes = new Hashtable(v.size());
	
	GeneratorInputStream generator = new GeneratorInputStream(input);
	try {
	    generator
		.setObjectOuputHandler(new ClassDescriptionHandler(classes));
	    while (true) {
		generator.readObject();
	    }
	} catch (EOFException e) {
	} finally {
	    generator.close();
	}
	
	return classes.elements();
    }
    
    /**
     * An objectOutputHandler to get all class description from
     * an GeneratorInputStream. 
     */    
    class ClassDescriptionHandler implements ObjectOutputHandler {
	Hashtable classes;
	int current = 0;
	
	ClassDescriptionHandler(Hashtable classes) {
	    this.classes = classes;
	}
	// uses only the method
	public void writeClassDescription(ClassDescription _class) 
	        throws IOException {
	    classes.put(_class.getName(), _class);
	}
	
	// all following methods are empty
	
	public void writeStartDocument() throws IOException {
	}
	public void writeEndDocument() throws IOException {
	}
	public void writeStartObject(int id, 
				     ClassDescription _class, 
				     boolean isTransien,
				     Field field)
	    throws IOException {
	}
	public void writeEndObject(int id, ClassDescription _class, Field field)
	    throws IOException {
	}
	public void writeStartObjectClass(int id, 
					  ClassDescription _class, 
					  boolean isTransient,
					  Field field)
	    throws IOException {}
	public void writeEndObjectClass(int id, 
					ClassDescription _class, 
					Field field) 
	    throws IOException {}
	public void writeStartSuper(ClassDescription _super) throws IOException {}
	public void writeEndSuper(ClassDescription _super) throws IOException {}
	public void writeStartArray(int id, ClassDescription _class, 
				    int size, boolean isTransient,
				    Field field)
	    throws IOException {}
	public void writeEndArray(int id, ClassDescription _class, Field field)
	    throws IOException {}
	public void writeNullReference(Field field) throws IOException {}
	public void writeReference(int id, Field field) throws IOException {}
	public void write(String value, boolean isTransient, Field field)
	    throws IOException {}
	public void write(boolean value, boolean isTransient, Field field)
	    throws IOException {}
	public void write(byte value, boolean isTransient, Field field)
	    throws IOException {}
	public void write(char value, boolean isTransient, Field field)
	    throws IOException {}
	public void write(short value, boolean isTransient, Field field)
	    throws IOException {}
	public void write(int value, boolean isTransient, Field field)
	    throws IOException {}
	public void write(long value, boolean isTransient, Field field)
	    throws IOException {}
	public void write(float value, boolean isTransient, Field field)
	    throws IOException {}
	public void write(double value, boolean isTransient, Field field)
	    throws IOException {}
	public void writeRow(byte[] data, int offset, int length) throws IOException {}
	public void close() throws IOException {}
	public void flush() throws IOException {}
    }
}
