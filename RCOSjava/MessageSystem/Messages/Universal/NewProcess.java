//******************************************************/
// FILE     : NewProcessMessage.java
// PURPOSE  :
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  :    Created
//******************************************************/

package MessageSystem.Messages.Universal;

import Hardware.Memory.Memory;
import Software.Process.ProcessScheduler;
import Software.Process.ProgramManager;
import Software.Process.RCOSProcess;
import Software.Kernel.Kernel;
import Hardware.CPU.Interrupt;
import MessageSystem.PostOffices.OS.OSMessageHandler;

/**
 * Used to send messages about creating new process.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 01/07/1997   Uses Memory
 * </DD><DD>
 * 03/08/97   Moved to message system
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th of March 1997
 */
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


  /**
   * Calculates the number of pages based on the memory block size.
   *
   * @return the number of whole code pages to store the file with.
   */
  public int getCodePages()
  {
    return ((int) (iFileSize / Memory.DEFAULT_SEGMENT)+1);
  }

  /**
   * Executes the newProcess method passing the stored process id..
   *
   * @param theElement the process scheduler to do the work on.
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.newProcess(this);
  }

  /**
   * Set to false so it is not recorded.
   */
  public boolean undoableMessage()
  {
    return false;
  }
}