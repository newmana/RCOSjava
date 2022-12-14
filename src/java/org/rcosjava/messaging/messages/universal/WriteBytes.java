package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.memory.MemoryManagerAnimator;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.memory.MemoryRequest;

/**
 * Write a series of bytes of memory.
 * <P>
 * @author Andrew Newman.
 * @created 3rd August 1997
 * @version 1.00 $Date$
 */
public class WriteBytes extends UniversalMessageAdapter
{
  /**
   * The memory request that is used by the receiving objects which holds such
   * things as the type, process id, and other defining qualities of the memory
   * to write to.
   */
  private MemoryRequest request;

  /**
   * Create a new write bytes message from a given source with a given memory
   * request item to store the type, process id and the new memory to write.
   *
   * @param theSource the sender of the message.
   * @param newRequest the object holding the details of the memory to write.
   */
  public WriteBytes(OSMessageHandler theSource, MemoryRequest newRequest)
  {
    super(theSource);
    request = newRequest;
  }

  /**
   * Allows a new request object to be sent as the payload. So that multiple
   * messages can be sent without calling the constructor.
   *
   * @param newRequest the request that is to be sent with the message.
   */
  public void setMemoryRequest(MemoryRequest newRequest)
  {
    request = newRequest;
  }

  /**
   * Calls writingMemory on the receiver.
   *
   * @param theElement the object being used to do the method.
   */
  public void doMessage(MemoryManagerAnimator theElement)
  {
    theElement.writingMemory(request);
  }

  /**
   * Calls writingBytes on the receiver.
   *
   * @param theElement the object being used to do the method.
   */
  public void doMessage(MemoryManager theElement)
  {
    theElement.writeBytes(request);
  }
}

