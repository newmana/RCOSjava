//******************************************************/
// FILE     : RemoveAllHandlersMessage.java
// PURPOSE  : Removing all message handlers (when post 
//            office is removed).
// AUTHOR   : Andrew Newman
// MODIFIED : 
// HISTORY  : 01/07/96   Created
//******************************************************/

package MessageSystem.Messages;

import MessageSystem.PostOffices.PostOffice;

public class RemoveAllHandlers extends MessageAdapter
{
  public void doMessage(PostOffice theElement)
  {
    theElement.clearHandlers();
  }
}