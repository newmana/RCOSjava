package net.sourceforge.rcosjava.messaging.messages;

import net.sourceforge.rcosjava.messaging.postoffices.PostOffice;
import net.sourceforge.rcosjava.messaging.postoffices.SimpleMessageHandler;

/**
 * Remove a handler from the
 * <P>
 * @author Andrew Newman
 * @version 1.00 $Date$
 * @created 1st July 1996
 */
public class RemoveHandler extends MessageAdapter
{
  public RemoveHandler(SimpleMessageHandler newSource)
  {
    super(newSource);
  }

  /**
   * Removes the handler (the object that sent the message) from the registered
   * handlers in the post office.
   */
  public void doMessage(PostOffice theElement)
  {
    theElement.removeHandler(getSource().getId());
  }
}