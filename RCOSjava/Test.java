import java.io.*;
import java.util.*;
import java.awt.*;
import java.net.*;
import fr.dyade.koala.xml.koml.*;

import org.xml.sax.*;

import fr.dyade.koala.serialization.GeneratorOutputStream;
import fr.dyade.koala.serialization.ObjectOutputHandler;import MessageSystem.PostOffices.OS.OSMessageHandler;
import MessageSystem.Messages.Universal.InstructionExecution;import Hardware.Memory.Memory;

/**
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
public class Test {

    /**
     * 
     */
    public static void serialize() throws Exception {
	KOMLSerializer ob = new KOMLSerializer("instances.xml", false);
	//ob.addObject(new InstructionExecution(new OSMessageHandler(),	//						 new Memory()));

	ob.close();
    }    
    
    /**
     * 
     */
    public static void deserialize() 
	    throws IOException, ClassNotFoundException {
	KOMLDeserializer koml = new KOMLDeserializer("instances.xml", false);


	try {
	    while (true) {
		System.err.println( koml.readObject() );
	    }
	} catch (EOFException e) {
	} finally {
	    koml.close();
	}
    }

    /**
     * 
     */
    public static void main(String[] args) throws Exception {
	try {
	    if (args.length == 0) {
		serialize();
	    } else if (args.length == 1) {
		deserialize();
	    } 
	} catch (SAXException e) {
	    if (e.getException() != null) {
		e.getException().printStackTrace();
	    } else {
		e.printStackTrace();
	    }
	}
	System.exit( 0 );
    }
}


class PInteger implements Serializable {
    int i = 6;
}

