package net.sourceforge.rcosjava.messaging.messages;

import net.sourceforge.rcosjava.messaging.postoffices.SimpleMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.PostOffice;

/**
 * Removing all message handlers (when post office is removed).
 * <P>
 * @author Andrew Newman
 * @version 1.00 $Date$
 * @created 1st July 1996
 */
public class RemoveAllHandlers extends MessageAdapter
{
  /**
   * Creates a message with a known body (should be serializable) and a known
   * source.
   *
   * @param newSource sets the source of the message.
   * @param newBody the message contents.
   */
  public RemoveAllHandlers(SimpleMessageHandler newSource, Object newBody)
  {
    super(newSource, newBody);
  }

  /**
   * Upon receiving the message the post office clearHandlers is called.
   *
   * @param theElement the post office to remove all the handlers from.
   */
  public void doMessage(PostOffice theElement)
  {
    theElement.clearHandlers();
  }
}