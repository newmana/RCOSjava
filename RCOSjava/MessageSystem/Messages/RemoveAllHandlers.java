package MessageSystem.Messages;

import MessageSystem.PostOffices.PostOffice;

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
   * Upon receiving the message the post office clearHandlers is called.
   *
   * @param theElement the post office to remove all the handlers from.
   */
  public void doMessage(PostOffice theElement)
  {
    theElement.clearHandlers();
  }
}