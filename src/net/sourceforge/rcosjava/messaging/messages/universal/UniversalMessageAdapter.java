package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.animator.about.AboutAnimator;
import net.sourceforge.rcosjava.software.animator.cpu.CPUAnimator;
import net.sourceforge.rcosjava.software.animator.disk.DiskSchedulerAnimator;
import net.sourceforge.rcosjava.software.animator.filesystem.FileSystemAnimator;
import net.sourceforge.rcosjava.software.animator.ipc.IPCManagerAnimator;
import net.sourceforge.rcosjava.software.animator.multimedia.MultimediaAnimator;
import net.sourceforge.rcosjava.software.animator.process.ProcessManagerAnimator;
import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.software.animator.process.ProgramManagerAnimator;
import net.sourceforge.rcosjava.software.animator.terminal.TerminalManagerAnimator;
import net.sourceforge.rcosjava.software.disk.DiskScheduler;
import net.sourceforge.rcosjava.software.filesystem.FileSystemManager;
import net.sourceforge.rcosjava.software.ipc.IPC;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.memory.MemoryManager;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessage;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessage;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.RCOS;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.software.terminal.TerminalManager;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.SimpleMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.PostOffice;
import java.io.Serializable;

/**
 * Adapter for OSMessage and AnimatorMessage for messages bound to both OS
 * and Animator.
 * <P>
 * HISTORY: 03/07/98   Used double dispatch<BR>
 * <P>
 * @author Andrew Newman
 * @created 24th March 1996
 * @version 1.00 $Date$
 */
public class UniversalMessageAdapter extends MessageAdapter
  implements OSMessage, AnimatorMessage, Serializable
{
  public UniversalMessageAdapter()
  {
    super();
  }

  public UniversalMessageAdapter(OSMessageHandler theSource)
  {
    super(theSource);
  }

  public UniversalMessageAdapter(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  public UniversalMessageAdapter(SimpleMessageHandler theSource)
  {
    super(theSource);
  }

  public boolean forPostOffice(PostOffice myPostOffice)
  {
    String postOfficeId = myPostOffice.getId();
    return ((postOfficeId.compareTo(RCOS.animatorPostOfficeId) == 0) ||
            (postOfficeId.compareTo(RCOS.osPostOfficeId) == 0));
  }

  public String getAnimatorType()
  {
    return "Animator";
  }

  public String getOSType()
  {
    //This depends on the location of the message in the class.
    //Change the class, change how it returns the OSMessage type.
    return (getType().substring(getType().indexOf(".")+1));
  }

  public void doMessage(DiskScheduler theElement)
  {
  }

  public void doMessage(FileSystemManager theElement)
  {
  }

  public void doMessage(IPC theElement)
  {
  }

  public void doMessage(Kernel theElement)
  {
  }

  public void doMessage(MemoryManager theElement)
  {
  }

  public void doMessage(ProcessScheduler theElement)
  {
  }

  public void doMessage(ProgramManager theElement)
  {
  }

  public void doMessage(SoftwareTerminal theElement)
  {
  }

  public void doMessage(TerminalManager theElement)
  {
  }

  public void doMessage(AboutAnimator theElement)
  {
  }

  public void doMessage(CPUAnimator theElement)
  {
  }

  public void doMessage(DiskSchedulerAnimator theElement)
  {
  }

  public void doMessage(FileSystemAnimator theElement)
  {
  }

  public void doMessage(IPCManagerAnimator theElement)
  {
  }

  public void doMessage(MultimediaAnimator theElement)
  {
  }

  public void doMessage(ProcessManagerAnimator theElement)
  {
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
  }

  public void doMessage(ProgramManagerAnimator theElement)
  {
  }

  public void doMessage(TerminalManagerAnimator theElement)
  {
  }

  public boolean undoableMessage()
  {
    return true;
  }
}