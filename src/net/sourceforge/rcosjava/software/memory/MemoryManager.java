package net.sourceforge.rcosjava.software.memory;

import java.lang.Integer;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.software.memory.MemoryRequest;
import net.sourceforge.rcosjava.software.memory.MemoryReturn;
import net.sourceforge.rcosjava.software.memory.paged.PagedMemoryManagement;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.messaging.messages.universal.AllocatedPages;
import net.sourceforge.rcosjava.messaging.messages.universal.DeallocatedPages;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.FinishedMemoryRead;
import net.sourceforge.rcosjava.messaging.messages.universal.FinishedMemoryWrite;
import net.sourceforge.rcosjava.hardware.memory.Memory;

/**
 * Specific way to provide memory using pages.  Needs to be made more generic
 * for other memory managers (see FileSystem for an example).
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @version 1.00 $Date$
 * @created 27th March 1996
 */
public class MemoryManager extends OSMessageHandler
{

  /**
   * Defines the number and size (in bytes) of pages
   */
  public final static int MAX_PAGES = 20;

  /**
   * Currently the page size is the same as the hardware memory.  This is for
   * simplicity sake.  To increase or decreate this value more coding would be
   * required.
   */
  public final static int PAGE_SIZE = 1024;

  /**
   * CODE_SEGMENTS hold compiled executables code (read only).
   */
  public final static byte CODE_SEGMENT = 1;

  /**
   * STACK_SEGMENT hold working space (read/write).
   */
  public final static byte STACK_SEGMENT = 2;
  private static final String MESSENGING_ID = "MemoryManager";

  private PagedMemoryManagement thePageHandler;

  public MemoryManager(OSOffice aPostOffice)
  {
    super(MESSENGING_ID, aPostOffice);
    thePageHandler = new PagedMemoryManagement(MemoryManager.MAX_PAGES);
  }

  public PagedMemoryManagement getPagedMemoryManager()
  {
    return thePageHandler;
  }

  public void allocatePages(MemoryRequest mrRequest)
  {
    try
    {
      MemoryReturn mrReturn = thePageHandler.open(mrRequest.getPID(),
        mrRequest.getMemoryType(), mrRequest.getSize());
      AllocatedPages msg = new AllocatedPages(this, mrReturn);
      sendMessage(msg);
    }
    catch (MemoryOpenFailedException e)
    {
    }
  }

  /**
   * Called by the DeallocatePages message from the process scheduler.  The
   * process has ceased functioning and the memory pages allocated to it must be
   * removed.
   */
  public void deallocateMemory(int pid)
  {
    MemoryReturn returnedMemory = thePageHandler.close(pid);
    // Return message.
    if (returnedMemory.getSize() > 0)
    {
      DeallocatedPages msg = new DeallocatedPages(this, returnedMemory);
      sendMessage(msg);
    }
  }

  public Memory readPage(int pid, byte iType, int iOffset)
  {
    return(thePageHandler.readPage(pid, iType, iOffset));
  }

  public Memory readBytes(int pid, byte iType, int iSize, int iOffset)
  {
    return(thePageHandler.readBytes(pid, iType, iSize, iOffset));
  }

  public void writePage(int pid, byte iType, int iOffset, Memory newMemory)
  {
    thePageHandler.writePage(pid, iType, iOffset, newMemory);
  }

  public void writeBytes(int pid, byte iType, int iSize, int iOffset, Memory newMemory)
  {
    thePageHandler.writeBytes(pid, iType, iSize, iOffset, newMemory);
  }

  public void writeBytes(MemoryRequest request)
  {
    writeBytes(request.getPID(), request.getMemoryType(),
      request.getSize(), request.getOffset(), request.getMemory());
    FinishedMemoryWrite tmpMsg = new FinishedMemoryWrite(this, request);
    sendMessage(tmpMsg);
  }

  public void processMessage(OSMessageAdapter aMsg)
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.out.println("Error processing message: "+e);
      e.printStackTrace();
    }
  }

  public void processMessage(UniversalMessageAdapter aMsg)
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.out.println("Error processing message: "+e);
      e.printStackTrace();
    }
  }

  public int pageSize()
  {
    return PAGE_SIZE;
  }

  public int maxPages()
  {
    return MAX_PAGES;
  }
}

