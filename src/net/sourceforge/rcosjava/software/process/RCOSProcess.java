package net.sourceforge.rcosjava.software.process;

import net.sourceforge.rcosjava.hardware.cpu.Context;
import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.messaging.messages.universal.NewProcess;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Defines the default process used to represent a user process in the system.
 * A process has a state (or status), an id, a priority, file name, memory,
 * terminal id, and a CPU context.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 01/07/97 Modified to use Memory. AN
 * </DD><DD>
 * 02/07/97 Uses MMU to get code and stack. AN
 * </DD><DD>
 * 10/11/97 Added getters and setters and constants. AN
 * </DD></DD>
 * 8/5/2001 Implemented comparable interface. AN
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @version 1.00 $Date$
 * @created 24th March 1996
 */
public class RCOSProcess implements Serializable, Comparable
{
  /**
   * The numeric value of the lowest priority that a process can have.
   */
  public static final byte MINIMUM_PRIORITY = 1;

  /**
   * The numeric value of the default priority that a process can have.
   */
  public static final byte DEFAULT_PRIORITY = 50;

  /**
   * The numeric value of the highest priority that a process can have.
   */
  public static final byte MAXIMUM_PRIORITY = 100;

  /**
   * The numeric value of a process that is in a zombie state.
   */
  public static final byte ZOMBIE = 1;

  /**
   * The numeric value of a process that is in a ready state.
   */
  public static final byte READY = 2;

  /**
   * The numeric value of a process that is in a running state.
   */
  public static final byte RUNNING = 3;

  /**
   * The numeric value of a process that is in a blocked state.
   */
  public static final byte BLOCKED = 4;

  /**
   * The process id.
   */
  private int PID;

  /**
   * The actual priority (from MINIMUM_PRIORITY to MAXIMUM_PRIORITY) of the
   * process.
   */
  private int priority;

  /**
   * Current status (Running, Ready ..)
   */
  private byte status;

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
    priority = DEFAULT_PRIORITY;
    status = ZOMBIE;
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
   * probably).
   * @param newFileName the path (relative to the file server) of the code of
   * the program.
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
   * file name, file size, stack pages and code pages.
   */
  public RCOSProcess (int newPID, NewProcess newProcessBody)
  {
    this(newPID, newProcessBody.getFilename(), newProcessBody.getFileSize(),
         newProcessBody.getStackPages(),newProcessBody.getCodePages());
  }


  /**
   * Creates a new process with the available set of attributes available to
   * set at start up.
   *
   * @param newPID the automatically generated process id,
   * @param newFileName the filename of the processes code.
   * @param newFileSize the length in bytes of the process code.
   * @param newStackPages the number of stack pages to allocate for this process.
   * @param newCodePages the number of code pages (based on file size) for this
   * process.
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
   */
  public RCOSProcess(RCOSProcess oldProcess)
  {
    PID = oldProcess.PID;
    priority = oldProcess.priority;
    fileName = oldProcess.fileName;
    codePages = oldProcess.codePages;
    stackPages = oldProcess.stackPages;
    fileSize = oldProcess.fileSize;
    status = oldProcess.status;
    cpuTicks = oldProcess.cpuTicks;
    terminalId = oldProcess.terminalId;
    currentContext = (Context) oldProcess.currentContext.clone();
  }

  //getters and setters
  public int getPID()
  {
    return PID;
  }

  public void setPID(int newPID)
  {
    PID = newPID;
  }

  public int getPriority()
  {
    return priority;
  }

  public void setPriority(int newPriority)
  {
    priority = newPriority;
  }

  public String getFileName()
  {
    return fileName;
  }

  public int getFileSize()
  {
    return fileSize;
  }

  public byte getStatus()
  {
    return status;
  }

  public void setStatus(byte newStatus)
  {
    status = newStatus;
  }

  public void setContext(Context newContext)
  {
    currentContext = newContext;
  }

  public int getStackPages()
  {
    return stackPages;
  }

  public int getCodePages()
  {
    return codePages;
  }

  public int getCPUTicks()
  {
    return cpuTicks;
  }

  public void addToCPUTicks(int newCPUTicks)
  {
    cpuTicks = cpuTicks + newCPUTicks;
  }

  public String getTerminalId()
  {
    return terminalId;
  }

  public void setTerminalId(String newTerminalId)
  {
    terminalId = newTerminalId;
  }

  public Context getContext()
  {
    return currentContext;
  }

  public int compareTo(Object object)
  {
    if ((object != null) && (object.getClass().equals(this.getClass())))
    {
      RCOSProcess tmpProcess = (RCOSProcess) object;

      if (getPriority() < tmpProcess.getPriority())
        return -1;
      else if (getPriority() == tmpProcess.getPriority())
        return 0;
      else if (getPriority() > tmpProcess.getPriority())
        return 1;
    }
    throw new ClassCastException();
  }

  /**
   * @return true if the process id, priority, file name, file size, status,
   * number of stack pages and cpu ticks are the same.
   */
  public boolean equals(Object object)
  {
    if (object != null && (object.getClass().equals(this.getClass())))
    {
      RCOSProcess tmpProcess = (RCOSProcess) object;
      if ((getPID() == tmpProcess.getPID()) &&
          (getPriority() == tmpProcess.getPriority()) &&
          (getFileName() == tmpProcess.getFileName()) &&
          (getFileSize() == tmpProcess.getFileSize()) &&
          (getStatus() == tmpProcess.getStatus()) &&
          (getStackPages() == tmpProcess.getStackPages()) &&
          (getCPUTicks() == tmpProcess.getCPUTicks()))
      {
        return true;
      }
    }
    return false;
  }
}
