//******************************************************/
// FILE     : NewProcessMessage.java
// PURPOSE  : Used to send messages about creating new process
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/96   Created
//          : 01/07/96   Uses Memory
//          : 03/08/97   Moved to message system
//******************************************************/

package MessageSystem.Messages.Universal;

import Hardware.Memory.Memory;
import Software.Process.ProcessScheduler;
import Software.Process.ProgramManager;
import Software.Process.RCOSProcess;
import Software.Kernel.Kernel;
import Hardware.CPU.Interrupt;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class NewProcess extends UniversalMessageAdapter
{
  private String sFilename;
  private Memory mProcessMemory;
  private int iFileSize;

  public NewProcess(OSMessageHandler theSource, String sNewFilename, 
    Memory mNewProcessMemory, int iNewFileSize)
  {
    super(theSource);
    sFilename = sNewFilename;
    mProcessMemory = mNewProcessMemory;
    iFileSize = iNewFileSize;
  }

  public void setFilename(String sNewFilename)
  {
    sFilename = sNewFilename;
  }

  public String getFilename()
  {
    return sFilename;
  }

  public void setMemory(Memory mNewMemory)
  {
    mProcessMemory = mNewMemory;
  }

  public Memory getMemory()
  {
    return mProcessMemory;
  }

  public int getFileSize()
  {
    return iFileSize;
  }

  public void setFileSize(int iNewFileSize)
  {
    iFileSize = iNewFileSize;
  }

  public int getStackPages()
  {
    return 1;
  }

  //Calculates the number of pages
  //based on the memory block size.

  public int getCodePages()
  {
    return ((int) (iFileSize / Memory.DEFAULT_SEGMENT)+1);
  }

  public void doMessage(ProcessScheduler theElement)
  {
    theElement.newProcess(this);
  }  
}