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

  /**
   * The name of the manager to register with the post office.
   */
  private static final String MESSENGING_ID = "MemoryManager";

  /**
   * The page memory manager to use for memory management.
   */
  private PagedMemoryManagement thePageHandler;

  /**
   * Register the memory manager with the given post office and create the
   * page handler with the default number of pages.
   */
  public MemoryManager(OSOffice postOffice)
  {
    super(MESSENGING_ID, postOffice);
    thePageHandler = new PagedMemoryManagement(MemoryManager.MAX_PAGES);
  }

  /**
   * @return the page handler.
   */
  public PagedMemoryManagement getPagedMemoryManager()
  {
    return thePageHandler;
  }

  /**
   * Called by the allocatePages message from the process scheduler.  A new
   * process  has started and requires memory pages allocated to it.
   */
  public void allocatePages(MemoryRequest request)
  {
    try
    {
      MemoryReturn returnedMsg = thePageHandler.open(request.getPID(),
        request.getMemoryType(), request.getSize());
      AllocatedPages msg = new AllocatedPages(this, returnedMsg);
      sendMessage(msg);
    }
    catch (MemoryOpenFailedException e)
    {
      //To do : Handle exception correctly.
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

  /**
   * Read all pages for a particular process, type and offset and return all of
   * them as a single memory block.
   *
   * @param pid the process id that own the memory
   * @param type whether stack or program code memory to read
   * @param offset the offset within the contiguous block of memory to start
   * from
   * @return the total contiguous memory block found with the given parameters.
   */
  public Memory readPage(int pid, byte type, int offset)
  {
    return(thePageHandler.readPage(pid, type, offset));
  }

  public Memory readBytes(int pid, byte type, int size, int offset)
  {
    return(thePageHandler.readBytes(pid, type, size, offset));
  }

  /**
   * Write a section of memory of a particular process, type and offset and
   * set it to the contents of the given block of memory.
   *
   * @param pid the process id that own the memory
   * @param type whether stack or program code memory to read
   * @param offset the offset within the contiguous block of memory to start
   * from
   * @param newMemory the memory to set the block of memory to
   */
  public void writePage(int pid, byte type, int offset, Memory newMemory)
  {
    thePageHandler.writePage(pid, type, offset, newMemory);
  }

  public void writeBytes(int pid, byte type, int size, int offset,
    Memory newMemory)
  {
    thePageHandler.writeBytes(pid, type, size, offset, newMemory);
  }

  public void writeBytes(MemoryRequest request)
  {
    writeBytes(request.getPID(), request.getMemoryType(),
      request.getSize(), request.getOffset(), request.getMemory());
    FinishedMemoryWrite tmpMsg = new FinishedMemoryWrite(this, request);
    sendMessage(tmpMsg);
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

