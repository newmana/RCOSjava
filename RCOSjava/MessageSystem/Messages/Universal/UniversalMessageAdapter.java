//*******************************************************************/
// FILE     : UniversalMessageAdapter.java
// PURPOSE  : Adapter for OSMessage and AnimatorMessage
//            for messages bound to both OS and Animator
// AUTHOR   : Andrew Newman
// MODIFIED : 
// HISTORY  : 24/03/96   Created
//          : 03/07/98   Used double dispatch
//*******************************************************************/

package MessageSystem.Messages.Universal;

import java.lang.Object;
import Software.Animator.CPU.CPUAnimator;
import Software.Animator.Disk.DiskSchedulerAnimator;
import Software.Animator.FileSystem.FileSystemAnimator;
import Software.Animator.IPC.IPCManagerAnimator;
import Software.Animator.Multimedia.MultimediaAnimator;
import Software.Animator.Process.ProcessManagerAnimator;
import Software.Animator.Process.ProcessSchedulerAnimator;
import Software.Animator.Process.ProgramManagerAnimator;
import Software.Animator.Terminal.TerminalManagerAnimator;
import Software.Disk.DiskScheduler;
import Software.FileSystem.FileSystemManager;
import Software.IPC.IPC;
import Software.Kernel.Kernel;
import Software.Memory.MemoryManager;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.Messages.Animator.AnimatorMessage;
import MessageSystem.Messages.OS.OSMessage;
import Software.Process.ProcessScheduler;
import Software.Process.ProgramManager;
import RCOS;
import Software.Terminal.SoftwareTerminal;
import Software.Terminal.TerminalManager;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import MessageSystem.PostOffices.Animator.AnimatorMessageHandler;
import MessageSystem.PostOffices.SimpleMessageHandler;
import MessageSystem.PostOffices.PostOffice;

public class UniversalMessageAdapter extends MessageAdapter 
  implements OSMessage, AnimatorMessage
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
		String sPostOfficeID = myPostOffice.getID();
    return ((sPostOfficeID.compareTo(RCOS.sAnimatorPostOfficeID) == 0) ||
            (sPostOfficeID.compareTo(RCOS.sOSPostOfficeID) == 0));
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
}