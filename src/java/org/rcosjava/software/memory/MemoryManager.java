package org.rcosjava.software.memory;

import java.io.*;

import org.rcosjava.RCOS;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.messaging.messages.universal.AllocatedSharedMemoryPages;
import org.rcosjava.messaging.messages.universal.AllocatedPages;
import org.rcosjava.messaging.messages.universal.DeallocatedPages;
import org.rcosjava.messaging.messages.universal.FinishedMemoryWrite;
import org.rcosjava.messaging.messages.universal.SharedMemoryWrote;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.software.memory.paged.PagedMemoryManagement;

/**
 * Specific way to provide memory using pages. Needs to be made more generic for
 * other memory managers (see FileSystem for an example).
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @created 27th March 1996
 * @version 1.00 $Date$
 */
public class MemoryManager extends OSMessageHandler
{
  /**
   * Defines the number and size (in bytes) of pages
   */
  public final static int MAX_PAGES = 20;

  /**
   * Currently the page size is the same as the hardware memory. This is for
   * simplicity sake. To increase or decreate this value more coding would be
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
   * SHARED_SEGMENT space designed for shared memory (read/write).
   */
  public final static byte SHARED_SEGMENT = 3;

  /**
   * The name of the manager to register with the post office.
   */
  private final static String MESSENGING_ID = "MemoryManager";

  /**
   * The page memory manager to use for memory management.
   */
  private PagedMemoryManagement thePageHandler;

  /**
   * Register the memory manager with the given post office and create the page
   * handler with the default number of pages.
   *
   * @param postOffice Description of Parameter
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
   * Called by the allocatePages message from the process scheduler. A new
   * process has started and requires memory pages allocated to it.
   *
   * @param request the type of memory to create (type, size, etc).
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

  public void allocateSharedMemoryPages(MemoryRequest request, String shmName,
      int shmSize)
  {
    try
    {
      MemoryReturn returnedMsg = thePageHandler.open(request.getPID(),
          request.getMemoryType(), request.getSize());
      Memory memory = thePageHandler.getAllMemory(request.getPID(),
          request.getMemoryType());

      AllocatedSharedMemoryPages msg = new AllocatedSharedMemoryPages(this,
          shmName, shmSize, returnedMsg, memory);
      sendMessage(msg);
    }
    catch (MemoryOpenFailedException e)
    {
      //To do : Handle exception correctly.
    }
  }

  /**
   * Called by SharedMemoryWrite message to write the values given in the
   * request object of the given shared memory name.
   *
   * @param request the memory type to read, process id and memory to write.
   * @param shmName the unique id of the shared memory segment.
   */
  public void sharedMemoryWrite(MemoryRequest request, String shmName)
  {
    try
    {
      writeBytes(request.getPID(), request.getMemoryType(), request.getSize(),
          request.getOffset(), request.getMemory());

      Memory memory = thePageHandler.getAllMemory(request.getPID(),
          request.getMemoryType());

      // Then let others know
      SharedMemoryWrote wroteMessage = new SharedMemoryWrote(this, shmName,
          memory);
      sendMessage(wroteMessage);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Called by the DeallocatePages message from the process scheduler. The
   * process has ceased functioning and the memory pages allocated to it must be
   * removed.
   *
   * @param pid the process to remove all allocated memory.
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
   *      from
   * @return the total contiguous memory block found with the given parameters.
   */
  public Memory readPage(int pid, byte type, int offset)
  {
    return (thePageHandler.readPage(pid, type, offset));
  }

  /**
   * Description of the Method
   *
   * @param pid Description of Parameter
   * @param type Description of Parameter
   * @param size Description of Parameter
   * @param offset Description of Parameter
   * @return Description of the Returned Value
   */
  public Memory readBytes(int pid, byte type, int size, int offset)
  {
    return (thePageHandler.readBytes(pid, type, size, offset));
  }

  /**
   * Write a section of memory of a particular process, type and offset and set
   * it to the contents of the given block of memory.
   *
   * @param pid the process id that own the memory
   * @param type whether stack or program code memory to read
   * @param offset the offset within the contiguous block of memory to start
   *      from
   * @param newMemory the memory to set the block of memory to
   */
  public void writePage(int pid, byte type, int offset, Memory newMemory)
  {
    thePageHandler.writePage(pid, type, offset, newMemory);
  }

  /**
   * Description of the Method
   *
   * @param pid Description of Parameter
   * @param type Description of Parameter
   * @param size Description of Parameter
   * @param offset Description of Parameter
   * @param newMemory Description of Parameter
   */
  public void writeBytes(int pid, byte type, int size, int offset,
      Memory newMemory)
  {
    thePageHandler.writeBytes(pid, type, size, offset, newMemory);
  }

  /**
   * Description of the Method
   *
   * @param request Description of Parameter
   */
  public void writeBytes(MemoryRequest request)
  {
    writeBytes(request.getPID(), request.getMemoryType(), request.getSize(),
        request.getOffset(), request.getMemory());

    FinishedMemoryWrite tmpMsg = new FinishedMemoryWrite(this, request);
    sendMessage(tmpMsg);
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public int pageSize()
  {
    return PAGE_SIZE;
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public int maxPages()
  {
    return MAX_PAGES;
  }

  /**
   * Handle the serialization of the contents.
   */
  private void writeObject(ObjectOutputStream os) throws IOException
  {
    os.writeObject(thePageHandler);
  }

  /**
   * Handle deserialization of the contents.  Ensures non-serializable
   * components correctly created.
   *
   * @param is stream that is being read.
   */
  private void readObject(ObjectInputStream is) throws IOException,
      ClassNotFoundException
  {
    register(MESSENGING_ID, RCOS.getOSPostOffice());
    thePageHandler = (PagedMemoryManagement) is.readObject();
  }
}