//******************************************************/
// FILE     : RemoveHandlerMessage.java
// PURPOSE  : Register Message sent to Animators.
// AUTHOR   : Andrew Newman
// MODIFIED : 
// HISTORY  : 01/07/96   Created
//******************************************************/

package MessageSystem.Messages;

import MessageSystem.PostOffices.PostOffice;

public class RemoveHandler extends MessageAdapter
{
  public void doMessage(PostOffice theElement)
  {
    theElement.removeHandler(getSource().getID());
  }
}