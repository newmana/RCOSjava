package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.ipc.IPC;
import org.rcosjava.software.animator.ipc.IPCManagerAnimator;
import org.rcosjava.software.memory.MemoryRequest;
import org.rcosjava.software.memory.MemoryManager;

/**
 * MMU is responding after writing data.
 * <P>
 * @author Andrew Newman
 * @created 28 April 2002
 * @version 1.00 $Date$
 */
public class FinishedMemoryWrite extends UniversalMessageAdapter
{
  /**
   * Description of the Field
   */
  private MemoryRequest request;

  /**
   * Constructor for the FinishedMemoryWrite object
   *
   * @param theSource Description of Parameter
   * @param newRequest Description of Parameter
   */
  public FinishedMemoryWrite(OSMessageHandler theSource, MemoryRequest newRequest)
  {
    super(theSource);
    request = newRequest;
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.finishedWritingMemory(this.request);
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public boolean undoableMessage()
  {
    return false;
  }
}

