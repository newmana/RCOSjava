//******************************************************/
// FILE     : NoDeviceErrorMessage.java
// PURPOSE  : What to do when a message is sent to a
//            device that isn't registered with the
//            message handler.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/96   Created
//******************************************************/

package MessageSystem.Messages;

import MessageSystem.PostOffices.PostOffice;

public class NoDeviceError extends MessageAdapter
{
  private final String ERROR_MESSAGE = "No such device registered: ";

  public void doMessage(PostOffice theElement)
  {
    System.err.println("POST OFFICE " + ERROR_MESSAGE + this.getSource().getId() +
      " from " + this.getSource());
  }

  public void doMessage(Object theElement)
  {
    System.err.println(ERROR_MESSAGE + theElement.getClass().getName());
  }
}

