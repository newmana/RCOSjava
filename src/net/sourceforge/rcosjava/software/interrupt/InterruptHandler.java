package net.sourceforge.rcosjava.software.interrupt;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import java.io.Serializable;

/**
 * Abstract class for InterruptHandlers.  Each DeviceDriver that must declare
 * and register appropriate InterruptHandlers with the Kernel
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 24/03/96 Modified to extend SimpleMessageHandler
 * </DD></DT>
 * <P>
 * @see net.sourceforge.rcosjava.messaging.postoffices.SimpleMessageHandler
 * @author Andrew Newman
 * @author Bruce Jamieson
 * @created 23 March 1996
 * @version 1.00 $Date$
 */
public class InterruptHandler extends OSMessageHandler implements Serializable
{
//  public static final String IH_IDENTIFIER = "IH";
  public String type;
  public static String IH_IDENTIFIER = "IH";

  public InterruptHandler(String sID, OSOffice mhPostOffice, String newType)
  {
    super((sID+IH_IDENTIFIER), mhPostOffice);
    type = newType;
  }

  public String getType()
  {
    return type;
  }

  /**
   * Had to change this from an abstract method because of crashing.
   */
  public void handleInterrupt()
  {
  }
  // perform any necesary clean up procedures so next
  // Interrupt of this type can occur
  // send a message to the appropriate DeviceDriver

  public void processMessage(UniversalMessageAdapter os)
  {
  }

  public void processMessage(OSMessageAdapter os)
  {
  }
}