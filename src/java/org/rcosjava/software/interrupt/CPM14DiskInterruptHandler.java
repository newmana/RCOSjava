package org.rcosjava.software.interrupt;
import java.io.Serializable;
import org.rcosjava.messaging.postoffices.os.OSOffice;

/**
 * Interrupt for CPM14 Disk.
 * <P>
 * @author Andrew Newman
 * @author Brett Carter
 * @created 25 March 1996
 * @version 1.00 $Date$
 */
public class CPM14DiskInterruptHandler extends InterruptHandler
     implements Serializable
{
  /**
   * Constructor for the CPM14DiskInterruptHandler object
   *
   * @param id Description of Parameter
   * @param postOffice Description of Parameter
   * @param newType Description of Parameter
   */
  public CPM14DiskInterruptHandler(String id, OSOffice postOffice,
      String newType)
  {
    super(id, postOffice, newType);
  }


  /**
   * Description of the Method
   */
  public void handleInterrupt()
  {
    // Clean up for next interupt??

    // Send message.
//    MessageAdapter aMessage = new MessageAdapter(getId(), theDestination, "DiskRequestComplete",
//                                     null);
//    SendMessage(aMessage);
  }

}
