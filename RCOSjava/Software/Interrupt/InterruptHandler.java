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

package Software.Interrupt;

import MessageSystem.PostOffices.OS.OSOffice;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;

abstract public class InterruptHandler extends OSMessageHandler
{
  public String sType, sDestination;
  public static final String IH_IDENTIFIER = "IH";
 
  public InterruptHandler(String sID, OSOffice mhPostOffice, 
    String sType, String sDestination)
  {
    super((sID+IH_IDENTIFIER), mhPostOffice);
    this.sType = sType;
    this.sDestination = sDestination;    
  }

  abstract public void handleInterrupt();
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