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

package Software.Animator.Process;

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import Software.Animator.*;
import MessageSystem.Messages.Universal.TerminalOn;
import MessageSystem.Messages.Universal.NewFile;
import MessageSystem.PostOffices.SimpleMessageHandler;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.PostOffices.Animator.AnimatorOffice;
import MessageSystem.PostOffices.PostOffice;
import Software.Animator.Process.ProgramManagerAnimator;
import Software.Process.*;
import Software.Util.*;
import Software.Terminal.*;

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
