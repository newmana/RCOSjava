//**************************************************************************/
// FILE     : InterruptHandler.java
// PACKAGE  :
// PURPOSE  : Abstract class for InterruptHandlers
//            Each DeviceDriver that must declare and register
//            appropriate InterruptHandlers with the Kernel
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 23/03/96	Created
//            24/03/96   modified to extend SimpleMessageHandler
//
//**************************************************************************/

package net.sourceforge.rcosjava.software.interrupt;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import java.io.Serializable;

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

  // Had to change this from an abstract method because of crashing.
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