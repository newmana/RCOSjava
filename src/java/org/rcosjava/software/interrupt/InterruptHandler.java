package org.rcosjava.software.interrupt;
import java.io.Serializable;
import org.rcosjava.messaging.messages.os.OSMessageAdapter;
import org.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.messaging.postoffices.os.OSOffice;

/**
 * Abstract class for InterruptHandlers. Each DeviceDriver that must declare and
 * register appropriate InterruptHandlers with the Kernel
 * <P>
 * <DT> <B>History:</B>
 * <DD> 24/03/96 Modified to extend SimpleMessageHandler </DD> </DT>
 * <P>
 * @author Andrew Newman
 * @author Bruce Jamieson
 * @created 23 March 1996
 * @see org.rcosjava.messaging.postoffices.SimpleMessageHandler
 * @version 1.00 $Date$
 */
public class InterruptHandler extends OSMessageHandler implements Serializable
{
  /**
   * Description of the Field
   */
  public static String IH_IDENTIFIER = "IH";
//  public static final String IH_IDENTIFIER = "IH";
  /**
   * Description of the Field
   */
  public String type;

  /**
   * Constructor for the InterruptHandler object
   *
   * @param sID Description of Parameter
   * @param mhPostOffice Description of Parameter
   * @param newType Description of Parameter
   */
  public InterruptHandler(String sID, OSOffice mhPostOffice, String newType)
  {
    super((sID + IH_IDENTIFIER), mhPostOffice);
    type = newType;
  }

  /**
   * Gets the Type attribute of the InterruptHandler object
   *
   * @return The Type value
   */
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

  /**
   * Description of the Method
   *
   * @param os Description of Parameter
   */
  public void processMessage(UniversalMessageAdapter os)
  {
  }

  /**
   * Description of the Method
   *
   * @param os Description of Parameter
   */
  public void processMessage(OSMessageAdapter os)
  {
  }
}
