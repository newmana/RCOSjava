package net.sourceforge.rcosjava.messaging.messages;

import net.sourceforge.rcosjava.messaging.postoffices.SimpleMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.PostOffice;

/**
 * Adds a handler to a given object.  Each handler can have other handlers
 * attached to them that are informed when a message is sent.
 * <P>
 * @author Andrew Newman
 * @version 1.00 $Date$
 * @created 21st October 2000
 */
public class AddHandler extends MessageAdapter
{
  /**
   * The unique id of the handler to add.
   */
  private String deviceId;

  /**
   * The actual handler object to store.
   */
  private SimpleMessageHandler toRegisterTo;

  /**
   * Creates a new handler message.
   *
   * @param newSource the message handler that sent the message.
   * @param newDeviceId the key to associate the message handler with.
   * @param newToRegisterTo the actual message handler object.
   */
  public AddHandler(SimpleMessageHandler newSource, String newDeviceId,
      SimpleMessageHandler newToRegisterTo)
  {
    super(newSource);
    deviceId = newDeviceId;
    toRegisterTo = newToRegisterTo;
  }

  /**
   * On receival to a post office object the handler stored will be added to
   * the post office using the addHandler method.
   *
   * @param theElement the post office object to do the work on.
   */
  public void doMessage(PostOffice theElement)
  {
    theElement.addHandler(deviceId, toRegisterTo);
  }
}
