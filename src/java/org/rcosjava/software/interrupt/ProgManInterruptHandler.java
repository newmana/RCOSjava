package org.rcosjava.software.interrupt;
import java.io.Serializable;
import org.rcosjava.messaging.messages.os.GetNewFile;
import org.rcosjava.messaging.postoffices.os.OSOffice;

/**
 * Interrupt handler for program manager.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 1/7/1998 Should've been calling new message not NewProcess. </DD> </DT>
 * <P>
 * @author Andrew Newman
 * @author David Jones
 * @created 23 March 1996
 * @version 1.00 $Date$
 */
public class ProgManInterruptHandler extends InterruptHandler
{
  /**
   * Constructor for the ProgManInterruptHandler object
   *
   * @param myId Description of Parameter
   * @param postOffice Description of Parameter
   * @param newType Description of Parameter
   */
  public ProgManInterruptHandler(String myId, OSOffice postOffice,
      String newType)
  {
    super(myId, postOffice, newType);
  }

  /**
   * Description of the Method
   */
  public void handleInterrupt()
  {
    // perform any necesary clean up procedures so next
    // Interrupt of this type can occur
    // THERE IS NONE
    // send a message to the appropriate DeviceDriver
    GetNewFile message = new GetNewFile(this);

    sendMessage(message);
  }
}
