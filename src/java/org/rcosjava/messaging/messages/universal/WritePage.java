package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.memory.MemoryManagerAnimator;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.memory.MemoryRequest;

/**
 * Description of the Class
 * <P>
 * @author Andrew Newman
 * @created 28 April 2002
 * @version 1.00 $Date$
 */
public class WritePage extends UniversalMessageAdapter
{
  /**
   * Description of the Field
   */
  private MemoryRequest request;

  /**
   * Constructor for the WritePage object
   *
   * @param theSource Description of Parameter
   * @param newRequest Description of Parameter
   */
  public WritePage(OSMessageHandler theSource, MemoryRequest newRequest)
  {
    super(theSource);
    request = newRequest;
  }

  /**
   * Sets the MemoryRequest attribute of the WritePage object
   *
   * @param mrNewRequest The new MemoryRequest value
   */
  public void setMemoryRequest(MemoryRequest mrNewRequest)
  {
    request = mrNewRequest;
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(MemoryManagerAnimator theElement)
  {
    theElement.writingMemory(request);
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(MemoryManager theElement)
  {
    theElement.writePage(request.getPID(), request.getMemoryType(),
        request.getOffset(), request.getMemory());

    FinishedMemoryWrite message = new FinishedMemoryWrite(theElement, request);
    theElement.sendMessage(message);
  }
}

