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
public class InterruptHandler implements Serializable
{
  /**
   * The literal value that is unique for each interrupt handler giving the
   * type of interrupt.
   */
  private String type;

  /**
   * Create a new interrupt handler.
   *
   * @param newType the type of the interrupt (unique).
   */
  public InterruptHandler(String newType)
  {
    type = newType;
  }

  /**
   * Returns the type of interrupt.
   *
   * @return the type of interrupt.
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
}
