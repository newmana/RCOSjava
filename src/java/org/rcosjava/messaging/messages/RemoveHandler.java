package org.rcosjava.messaging.messages;
import org.rcosjava.messaging.postoffices.PostOffice;
import org.rcosjava.messaging.postoffices.SimpleMessageHandler;

/**
 * Remove a handler from the
 * <P>
 * @author Andrew Newman
 * @created 1st July 1996
 * @version 1.00 $Date$
 */
public class RemoveHandler extends MessageAdapter
{
  /**
   * Constructor for the RemoveHandler object
   *
   * @param newSource Description of Parameter
   */
  public RemoveHandler(SimpleMessageHandler newSource)
  {
    super(newSource);
  }

  /**
   * Removes the handler (the object that sent the message) from the registered
   * handlers in the post office.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(PostOffice theElement)
  {
    theElement.removeHandler(getSource().getId());
  }
}
