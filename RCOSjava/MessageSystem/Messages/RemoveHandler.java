package MessageSystem.Messages;

import MessageSystem.PostOffices.PostOffice;

/**
 * Remove a handler from the
 * <P>
 * @author Andrew Newman
 * @version 1.00 $Date$
 * @created 1st July 1996
 */
public class RemoveHandler extends MessageAdapter
{
  /**
   * Removes the handler (the object that sent the message) from the registered
   * handlers in the post office.
   */
  public void doMessage(PostOffice theElement)
  {
    theElement.removeHandler(getSource().getId());
  }
}