// *************************************************************************
// FILE     : StartProgram.java
// PURPOSE  : To communicate via messages and via awt Events with the main
//            RCOS frame and other components. It's functions are to set up
//            and comunicate with a remote server for the loading of
//            programs.  This one implements a seperate thread, however.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 1/5/97  Created.
//
// *************************************************************************

package net.sourceforge.rcosjava.software.animator.process;

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import net.sourceforge.rcosjava.software.animator.*;
import net.sourceforge.rcosjava.messaging.messages.universal.TerminalOn;
import net.sourceforge.rcosjava.messaging.messages.universal.NewFile;
import net.sourceforge.rcosjava.messaging.postoffices.SimpleMessageHandler;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import net.sourceforge.rcosjava.messaging.postoffices.PostOffice;
import net.sourceforge.rcosjava.software.animator.process.ProgramManagerAnimator;
import net.sourceforge.rcosjava.software.process.*;
import net.sourceforge.rcosjava.software.util.*;
import net.sourceforge.rcosjava.software.terminal.*;

public class StartProgram implements Runnable
{
  private ProgramManagerAnimator myAnimator;
  private Thread tThread;
  private String sFilename;
  private boolean bStartTerminal;
  private AnimatorOffice myPostOffice;

  public StartProgram (ProgramManagerAnimator newProgramManagerAnimator,
    AnimatorOffice mhNewPostOffice, String sNewFilename,
    boolean bNewStartTerminal)
  {
    myAnimator = newProgramManagerAnimator;
    myPostOffice = mhNewPostOffice;
    sFilename = sNewFilename;
    bStartTerminal = bNewStartTerminal;
    if (tThread == null)
    {
      tThread = new Thread(this, "Start Program");
      tThread.setPriority(tThread.MAX_PRIORITY);
      tThread.start();
    }
  }

  public void run()
  {
    // If the box is checked start a terminal before the process
    // is loaded.
    if(bStartTerminal)
    {
      TerminalOn msg = new TerminalOn(myAnimator);
      myPostOffice.sendMessage(msg);
    }
    NewFile msg = new NewFile(myAnimator, sFilename);
    myPostOffice.sendMessage(msg);
  }
}
