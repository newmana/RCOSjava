package Software.Process;

import Hardware.CPU.Context;
import Hardware.Memory.Memory;
import MessageSystem.Messages.Universal.NewProcess;
import java.io.Serializable;

/**
 * Defines the default process used to represent a user process in the system.
 * A process has a state (or status), an id, a priority, file name, memory,
 * terminal id, and a CPU context.
 * <P>
 * HISTORY: 01/07/97  Modified to use Memory. AN<BR>
 *          02/07/97  Uses MMU to get code and stack. AN<BR>
 *          10/11/97  Added getters and setters and constants. AN<BR>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @version 1.00 $Date$
 * @created 24th March 1996
 */
public class RCOSProcess implements Serializable
{
  public static final byte MINIMUM_PRIORITY = 1;
  public static final byte DEFAULT_PRIORITY = 50;
  public static final byte MAXIMUM_PRIORITY = 100;
  public static final byte ZOMBIE = 1;
  public static final byte READY = 2;
  public static final byte RUNNING = 3;
  public static final byte BLOCKED = 4;
  private int iPID, iPriority; // process Id and priority
  private byte bStatus;     // current status (Running, Ready ...)
  private String sFileName;   // filename for process code
  private int iFileSize;      // size of process' code (in bytes)
  private int iStackPages;    // Number of stack pages.
  private int iCodePages;     // Number of code pages.
  private String sTerminalID; // string id for process' terminal
  private int iCPUTicks;      // time spent on the CPU
  private Context cContext;   // current CPU context

  public RCOSProcess()
  {
    iPID = 0;
    iPriority = DEFAULT_PRIORITY;
    bStatus = ZOMBIE;
    sFileName = null;
    iFileSize = 0;
    iStackPages = 0;
    iCodePages = 0;
    sTerminalID = null;
    iCPUTicks = 0;
    cContext = new Context();
  }

  public RCOSProcess(int iNewPID, String sNewFileName)
  {
    this();
    iPID = iNewPID;
    sFileName = sNewFileName;
  }

  public RCOSProcess (int iNewPID, NewProcess npmNewBody)
  {
    this(iNewPID, npmNewBody.getFilename(), npmNewBody.getFileSize(),
         npmNewBody.getStackPages(),npmNewBody.getCodePages());
  }


  public RCOSProcess(int iNewPID, String sNewFileName, int iNewFileSize,
                     int iNewStackPages, int iNewCodePages)
  {
    this(iNewPID, sNewFileName);
    iFileSize = iNewFileSize;
    iStackPages = iNewStackPages;
    iCodePages = iNewCodePages;
  }


  public RCOSProcess(RCOSProcess rpOld)
  {
    iPID = rpOld.iPID;
    iPriority = rpOld.iPriority;
    sFileName = rpOld.sFileName;
    iCodePages = rpOld.iCodePages;
    iStackPages = rpOld.iStackPages;
    iFileSize = rpOld.iFileSize;
    bStatus = rpOld.bStatus;
    iCPUTicks = rpOld.iCPUTicks;
    sTerminalID = rpOld.sTerminalID;
    cContext = (Context) rpOld.cContext.clone();
  }

  //getters and setters

  public int getPID()
  {
    return iPID;
  }

  public void setPID(int iNewPID)
  {
    iPID = iNewPID;
  }

  public int getPriority()
  {
    return iPriority;
  }

  public String getFileName()
  {
    return sFileName;
  }

  public int getFileSize()
  {
    return iFileSize;
  }

  public byte getStatus()
  {
    return bStatus;
  }

  public void setStatus(byte bNewStatus)
  {
    bStatus = bNewStatus;
  }

  public void setContext(Context newContext)
  {
    cContext = newContext;
  }

  public int getStackPages()
  {
    return iStackPages;
  }

  public int getCodePages()
  {
    return iCodePages;
  }

  public int getCPUTicks()
  {
    return iCPUTicks;
  }

  public void addToCPUTicks(int iNewCPUTicks)
  {
    iCPUTicks = iCPUTicks + iNewCPUTicks;
  }

  public String getTerminalID()
  {
    return sTerminalID;
  }

  public void setTerminalID(String sNewTerminalID)
  {
    sTerminalID = sNewTerminalID;
  }

  public Context getContext()
  {
    return cContext;
  }
}
