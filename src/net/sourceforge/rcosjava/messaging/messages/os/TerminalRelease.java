//***************************************************************************
// FILE    : TerminalReleaseMessage.java
// PACKAGE : Terminal
// PURPOSE :
// AUTHOR  : Andrew Newman
// MODIFIED:
// HISTORY : 28/03/96  Created
//
//
//***************************************************************************

package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.software.terminal.TerminalManager;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

public class TerminalRelease extends OSMessageAdapter
{
  private String sTerminalID;

  public TerminalRelease(OSMessageHandler theSource,
    String sNewTerminalID)
  {
    super(theSource);
    sTerminalID = sNewTerminalID;
  }

  public void setValues(String sNewTerminalID)
  {
    sTerminalID = sNewTerminalID;
  }

  public void doMessage(TerminalManager theElement)
  {
    theElement.removeTerminal(sTerminalID);
  }
}

