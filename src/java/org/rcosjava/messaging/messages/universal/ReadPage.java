package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.memory.MemoryManagerAnimator;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.memory.MemoryRequest;

/**
 * Description of the Class
 *
 * @author Andrew Newman
 * @created 28 April 2002
 * @version 1.00 $Date$
 */
public class ReadPage extends UniversalMessageAdapter
{
  /**
   * Description of the Field
   */
  private MemoryRequest request;

  /**
   * Constructor for the ReadPage object
   *
   * @param theSource Description of Parameter
   * @param mrNewRequest Description of Parameter
   */
  public ReadPage(OSMessageHandler theSource, MemoryRequest newRequest)
  {
    super(theSource);
    request = newRequest;
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(MemoryManagerAnimator theElement)
  {
    theElement.readingMemory(request);
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(MemoryManager theElement)
  {
    request.setMemory(theElement.readPage(request.getPID(),
        request.getMemoryType(), request.getOffset()));

    FinishedMemoryRead message = new FinishedMemoryRead(theElement, request);
    theElement.sendMessage(message);
  }
}

