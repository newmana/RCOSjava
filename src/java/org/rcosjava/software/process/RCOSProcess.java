package org.rcosjava.software.process;
import java.io.Serializable;
import java.util.Comparator;
import org.rcosjava.hardware.cpu.Context;
import org.rcosjava.messaging.messages.universal.NewProcess;

/**
 * Defines the default process used to represent a user process in the system. A
 * process has a state (or status), an id, a priority, file name, memory,
 * terminal id, and a CPU context.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 01/07/97 Modified to use Memory. AN </DD>
 * <DD> 02/07/97 Uses MMU to get code and stack. AN </DD>
 * <DD> 10/11/97 Added getters and setters and constants. AN </DD>
 * <DD> 8/5/2001 Implemented comparable interface. AN </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @created 24th March 1996
 * @version 1.00 $Date$
 */
public class RCOSProcess implements Serializable, Comparable
{
  /**
   * The process id.
   */
  private int PID;

  /**
   * The actual priority (from MINIMUM_PRIORITY to MAXIMUM_PRIORITY) of the
   * process.
   */
  private ProcessPriority priority;

  /**
   * Current status (Running, Ready ..)
   */
  private ProcessState state;

  /**
   * Filename for process code
   */
  private String fileName;

  /**
   * Size of process' code (in bytes)
   */
  private int fileSize;

  /**
   * Number of stack pages.
   */
  private int stackPages;

  /**
   * Number of code pages.
   */
  private int codePages;

  /**
   * String id for process' terminal
   */
  private String terminalId;

  /**
   * Time spent on the CPU
   */
  private int cpuTicks;

  /**
   * Current CPU context
   */
  private Context currentContext;

  /**
   * Creates a process with a PID of 0, priority of DEFAULT, a ZOMBIE status,
   * with no filename, filesize, stackpages, etc.
   */
  public RCOSProcess()
  {
    PID = 0;
    priority = ProcessPriority.DEFAULT_PRIORITY;
    state = ProcessState.ZOMBIE;
    fileName = null;
    fileSize = 0;
    stackPages = 0;
    codePages = 0;
    terminalId = null;
    cpuTicks = 0;
    currentContext = new Context();
  }

  /**
   * Creates a new process with the given PID and filename.
   *
   * @param newPID the given PID (generated from the Process Scheduler
   *      probably).
   * @param newFileName the path (relative to the file server) of the code of
   *      the program.
   */
  public RCOSProcess(int newPID, String newFileName)
  {
    this();
    PID = newPID;
    fileName = newFileName;
  }

  /**
   * Creates a given process with the NewProcess object (which is a sub-set of
   * the information stored here) and a given process ID.
   *
   * @param newPID the process id of the new process (generated automatically).
   * @param newProcessBody contains the values to set to this object such as the
   *      file name, file size, stack pages and code pages.
   */
  public RCOSProcess(int newPID, NewProcess newProcessBody)
  {
    this(newPID, newProcessBody.getFilename(), newProcessBody.getFileSize(),
        newProcessBody.getStackPages(), newProcessBody.getCodePages());
  }

  /**
   * Creates a new process with the available set of attributes available to set
   * at start up.
   *
   * @param newPID the automatically generated process id,
   * @param newFileName the filename of the processes code.
   * @param newFileSize the length in bytes of the process code.
   * @param newStackPages the number of stack pages to allocate for this
   *      process.
   * @param newCodePages the number of code pages (based on file size) for this
   *      process.
   */
  public RCOSProcess(int newPID, String newFileName, int newFileSize,
      int newStackPages, int newCodePages)
  {
    this(newPID, newFileName);
    fileSize = newFileSize;
    stackPages = newStackPages;
    codePages = newCodePages;
  }

  /**
   * Accepts an existing RCOSProcess to create a new copy.
   *
   * @param oldProcess Description of Parameter
   */
  public RCOSProcess(RCOSProcess oldProcess)
  {
    PID = oldProcess.getPID();
    priority = oldProcess.getPriority();
    fileName = oldProcess.getFileName();
    codePages = oldProcess.getCodePages();
    stackPages = oldProcess.getStackPages();
    fileSize = oldProcess.getFileSize();
    state = oldProcess.getState();
    cpuTicks = oldProcess.cpuTicks;
    terminalId = oldProcess.terminalId;
    currentContext = (Context) oldProcess.currentContext.clone();
  }

  /**
   * Sets the PID attribute of the RCOSProcess object
   *
   * @param newPID The new PID value
   */
  public void setPID(int newPID)
  {
    PID = newPID;
  }

  /**
   * Sets the Priority attribute of the RCOSProcess object
   *
   * @param newPriority The new Priority value
   */
  public void setPriority(ProcessPriority newPriority)
  {
    priority = newPriority;
  }

  /**
   * Sets the Status attribute of the RCOSProcess object
   *
   * @param newStatus The new Status value
   */
  public void setStatus(ProcessState newState)
  {
    state = newState;
  }

  /**
   * Sets the Context attribute of the RCOSProcess object
   *
   * @param newContext The new Context value
   */
  public void setContext(Context newContext)
  {
    currentContext = newContext;
  }

  /**
   * Sets the TerminalId attribute of the RCOSProcess object
   *
   * @param newTerminalId The new TerminalId value
   */
  public void setTerminalId(String newTerminalId)
  {
    terminalId = newTerminalId;
  }

  //getters and setters
  /**
   * Gets the PID attribute of the RCOSProcess object
   *
   * @return The PID value
   */
  public int getPID()
  {
    return PID;
  }

  /**
   * Gets the Priority attribute of the RCOSProcess object
   *
   * @return The Priority value
   */
  public ProcessPriority getPriority()
  {
    return priority;
  }

  /**
   * Gets the FileName attribute of the RCOSProcess object
   *
   * @return The FileName value
   */
  public String getFileName()
  {
    return fileName;
  }

  /**
   * Gets the FileSize attribute of the RCOSProcess object
   *
   * @return The FileSize value
   */
  public int getFileSize()
  {
    return fileSize;
  }

  /**
   * Gets the Status attribute of the RCOSProcess object
   *
   * @return The Status value
   */
  public ProcessState getState()
  {
    return state;
  }

  /**
   * Gets the StackPages attribute of the RCOSProcess object
   *
   * @return The StackPages value
   */
  public int getStackPages()
  {
    return stackPages;
  }

  /**
   * Gets the CodePages attribute of the RCOSProcess object
   *
   * @return The CodePages value
   */
  public int getCodePages()
  {
    return codePages;
  }

  /**
   * Gets the CPUTicks attribute of the RCOSProcess object
   *
   * @return The CPUTicks value
   */
  public int getCPUTicks()
  {
    return cpuTicks;
  }

  /**
   * Gets the TerminalId attribute of the RCOSProcess object
   *
   * @return The TerminalId value
   */
  public String getTerminalId()
  {
    return terminalId;
  }

  /**
   * Gets the Context attribute of the RCOSProcess object
   *
   * @return The Context value
   */
  public Context getContext()
  {
    return currentContext;
  }

  /**
   * Adds a feature to the ToCPUTicks attribute of the RCOSProcess object
   *
   * @param newCPUTicks The feature to be added to the ToCPUTicks attribute
   */
  public void addToCPUTicks(int newCPUTicks)
  {
    cpuTicks = cpuTicks + newCPUTicks;
  }

  /**
   * Compares the object based on priority value.
   *
   * @param object RCOSPRocess object to compare with.
   * @return -1 if the process of this object is less than the given process, 0
   *      if it is equal and 1 if it is greater than.
   */
  public int compareTo(Object object)
  {
    if ((object != null) && (object.getClass().equals(this.getClass())))
    {
      RCOSProcess tmpProcess = (RCOSProcess) object;
      return getPriority().compareTo(tmpProcess.getPriority());
    }
    throw new ClassCastException();
  }

  /**
   * @param object Description of Parameter
   * @return true if the process id, priority, file name, file size, status,
   *      number of stack pages and cpu ticks are the same.
   */
  public boolean equals(Object object)
  {
    boolean result = false;

    if (object != null && (object.getClass().equals(this.getClass())))
    {
      RCOSProcess tmpProcess = (RCOSProcess) object;

      if ((getPID() == tmpProcess.getPID()) &&
          (getPriority() == tmpProcess.getPriority()) &&
          (getFileName() == tmpProcess.getFileName()) &&
          (getFileSize() == tmpProcess.getFileSize()) &&
          (getState() == tmpProcess.getState()) &&
          (getStackPages() == tmpProcess.getStackPages()) &&
          (getCPUTicks() == tmpProcess.getCPUTicks()))
      {
        result = true;
      }
    }
    return result;
  }
}
