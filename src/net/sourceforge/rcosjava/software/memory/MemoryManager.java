//****************************************************************/
// FILE     : MemoryManager.java
// PURPOSE  : Specific way to provide memory using pages.  Needs
//            to be made more generic for other memory managers
//            (see FileSystem for an example).
// AUTHOR   : Bruce Jamieson
// MODIFIED : Andrew Newman
// HISTORY  : 27/03/96  (Last Modified)
//
//****************************************************************/

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

public class MemoryManager extends OSMessageHandler
{
  //Defines the number and size (in bytes) of
  //pages;
  public final static int MAX_PAGES = 20;
  // Currently the page size is the same as the hardware memory.
  // This is for simplicity sake.  To increase or decreate this
  // value more coding would be required.
  public final static int PAGE_SIZE = 1024;
  // Defines two types of memory.
  //CODE_SEGMENTS hold compiled executables.
  public final static byte CODE_SEGMENT = 1;
  //STACK_SEGMENT hold working space.
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

  public void deallocatePages(int iPID)
  {
    MemoryReturn mrReturn = thePageHandler.close(iPID);
    // Return message.
    if (mrReturn.getSize() > 0)
    {
      DeallocatedPages msg = new DeallocatedPages(this, mrReturn);
      sendMessage(msg);
    }
  }

  public Memory readPage(int iPID, int iType, int iOffset)
  {
    return(thePageHandler.readPage(iPID, iType, iOffset));
  }

  public Memory readBytes(int iPID, int iType, int iSize, int iOffset)
  {
    return(thePageHandler.readBytes(iPID, iType, iSize, iOffset));
  }

  public void writePage(int iPID, int iType, int iOffset, Memory newMemory)
  {
    thePageHandler.writePage(iPID, iType, iOffset, newMemory);
  }

  public void writeBytes(int iPID, int iType, int iSize, int iOffset, Memory newMemory)
  {
    thePageHandler.writeBytes(iPID, iType, iSize, iOffset, newMemory);
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

  public synchronized int pageSize()
  {
    return PAGE_SIZE;
  }

  public synchronized int maxPages()
  {
    return MAX_PAGES;
  }
}
