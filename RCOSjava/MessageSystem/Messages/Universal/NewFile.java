//***************************************************************************
// FILE    : NewFileMessage.java
// PACKAGE :
// PURPOSE :
// AUTHOR  : Andrew Newman
// MODIFIED:
// HISTORY : 28/03/96  Created
//
//
//***************************************************************************

package MessageSystem.Messages.Universal;

import Software.Process.ProgramManager;
import MessageSystem.Messages.Universal.NewProcess;
import MessageSystem.Messages.OS.HandleInterrupt;
import MessageSystem.PostOffices.SimpleMessageHandler;
import Software.Kernel.Kernel;
import Hardware.CPU.Interrupt;
import Software.Animator.Process.StartProgram;

public class NewFile extends UniversalMessageAdapter
{
  private String sFilename;

  public NewFile(SimpleMessageHandler theSource, String sNewFilename)
  {
    super(theSource);
    sFilename = sNewFilename;
  }

  private void setFilename(String sNewFilename)
  {
    sFilename = sNewFilename;
  }

  public void doMessage(ProgramManager theElement)

    //Send a new process interrupt to the Kernel
  {
    theElement.setNewFilename(sFilename);
    // Create a new message body to send to Process Scheduler.
    // Contains file information and code
    theElement.open();
    NewProcess newMsg = new NewProcess(theElement,
      sFilename, theElement.getFileContents(sFilename),
      theElement.getFileSize(sFilename));
    theElement.close();
    theElement.sendMessage(newMsg);

    Interrupt intInterrupt = new Interrupt(-1, "NewProcess");
    HandleInterrupt newMsg2 = new HandleInterrupt(
      theElement, intInterrupt);
    theElement.sendMessage(newMsg2);
  }
}

