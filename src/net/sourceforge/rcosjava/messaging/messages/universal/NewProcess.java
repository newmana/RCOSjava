//******************************************************/
// FILE     : NewProcessMessage.java
// PURPOSE  :
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  :    Created
//******************************************************/

package net.sourceforge.rcosjava.messaging.messages.universal;


import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.hardware.cpu.Interrupt;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

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
   * Executes the newProcess method passing the stored process id.
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