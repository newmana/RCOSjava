package MessageSystem.Messages;

import MessageSystem.PostOffices.SimpleMessageHandler;
import MessageSystem.PostOffices.PostOffice;

/**
 *  Register Message sent to Animators.
 *
 * @author     Andrew Newman
 * @created    21 October 2000
 */
public class AddHandler extends MessageAdapter
{
  private String sDeviceID;
  private SimpleMessageHandler mhToRegisterTo;

  /**
   *  Constructor for the AddHandler object
   *
   * @param  theSource          Description of Parameter
   * @param  sNewDeviceID       Description of Parameter
   * @param  mhNewToRegisterTo  Description of Parameter
   */
  public AddHandler(SimpleMessageHandler theSource, String sNewDeviceID,
      SimpleMessageHandler mhNewToRegisterTo)
  {
    super(theSource);
    sDeviceID = sNewDeviceID;
    mhToRegisterTo = mhNewToRegisterTo;
  }


  /**
   *  Description of the Method
   *
   * @param  theElement  Description of Parameter
   */
  public void doMessage(PostOffice theElement)
  {
    theElement.addHandler(sDeviceID, mhToRegisterTo);
  }
}
