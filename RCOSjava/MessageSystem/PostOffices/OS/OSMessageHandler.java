package MessageSystem.PostOffices.OS;

import MessageSystem.PostOffices.SimpleMessageHandler;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.PostOffices.PostOffice;
import MessageSystem.Messages.AddHandler;

/**
 *  Provide sending and receiving facilities for all classes.
 *
 * @author     Bruce Jamieson, Andrew Newman
 * @created    21 October 2000
 */
public abstract class OSMessageHandler extends SimpleMessageHandler
{
  /**
   *  Description of the Field
   */
  protected OSOffice mhThePostOffice;

  /**
   *  Constructor for the OSMessageHandler object
   */
  public OSMessageHandler()
  {
  }

  /**
   *  Constructor for the OSMessageHandler object
   *
   * @param  newID            Description of Parameter
   * @param  mhNewPostOffice  Description of Parameter
   */
  public OSMessageHandler(String newID, OSOffice mhNewPostOffice)
  {
    id = newID;
    // Save myId and a pointer the PostOffice
    mhThePostOffice = mhNewPostOffice;
    // Tell the PostOffice that I'm alive
    AddHandler newMessage = new AddHandler(this, newID, this);
    mhNewPostOffice.processMessage(newMessage);
  }

  /**
   *  Description of the Method
   *
   * @param  aMessage  Description of Parameter
   */
  public void sendMessage(MessageAdapter aMessage)
  {
    mhThePostOffice.sendMessage(aMessage);
  }

  /**
   *  Description of the Method
   *
   * @param  aMessage  Description of Parameter
   */
  public void sendMessage(UniversalMessageAdapter aMessage)
  {
    mhThePostOffice.sendMessage(aMessage);
  }

  /**
   *  Description of the Method
   *
   * @param  aMessage  Description of Parameter
   */
  public void sendMessage(OSMessageAdapter aMessage)
  {
    localSendMessage(aMessage);
  }

  /**
   *  Description of the Method
   *
   * @param  aMessage  Description of Parameter
   */
  public void localSendMessage(MessageAdapter aMessage)
  {
    mhThePostOffice.localSendMessage(aMessage);
  }

  /**
   *  Description of the Method
   *
   * @param  aMessage  Description of Parameter
   */
  public void localSendMessage(OSMessageAdapter aMessage)
  {
    mhThePostOffice.localSendMessage(aMessage);
  }

  /**
   *  Description of the Method
   *
   * @param  mMessage  Description of Parameter
   */
  public void processMessage(MessageAdapter mMessage)
  {
    try
    {
      processMessage((UniversalMessageAdapter) mMessage);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   *  Description of the Method
   *
   * @param  mMessage  Description of Parameter
   */
  public abstract void processMessage(OSMessageAdapter mMessage);

  /**
   *  Description of the Method
   *
   * @param  mMessage  Description of Parameter
   */
  public abstract void processMessage(UniversalMessageAdapter mMessage);
}
