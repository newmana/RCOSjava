package MessageSystem.PostOffices.OS;

import MessageSystem.PostOffices.SimpleMessageHandler;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.PostOffices.PostOffice;
import MessageSystem.Messages.AddHandler;

/**
 * Provide sending and receiving facilities for all classes.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 21/03/2001 Fixed local send message to call process message instead of
 * local send message of the post office (class cast exception).
 * </DD></DT>
 *
 * @author Andrew Newman
 * @author Bruce Jamieson
 * @created 21 October 2000
 * @version 1.00 $Date$
 */
public abstract class OSMessageHandler extends SimpleMessageHandler
{
  /**
   *  Description of the Field
   */
  protected OSOffice postOffice;

  /**
   *  Constructor for the OSMessageHandler object
   */
  public OSMessageHandler()
  {
  }

  /**
   * Constructor for the OSMessageHandler object
   *
   * @param newId
   * @param newPostOffice
   */
  public OSMessageHandler(String newId, OSOffice newPostOffice)
  {
    id = newId;
    // Save myId and a pointer the PostOffice
    postOffice = newPostOffice;
    // Tell the PostOffice that I'm alive
    AddHandler newMessage = new AddHandler(this, newId, this);
    newPostOffice.processMessage(newMessage);
  }

  /**
   *  Description of the Method
   *
   * @param  adapter  Description of Parameter
   */
  public void sendMessage(MessageAdapter adapter)
  {
    postOffice.sendMessage(adapter);
  }

  /**
   *  Description of the Method
   *
   * @param  adapter  Description of Parameter
   */
  public void sendMessage(UniversalMessageAdapter adapter)
  {
    postOffice.sendMessage(adapter);
  }

  /**
   *  Description of the Method
   *
   * @param  adapter  Description of Parameter
   */
  public void sendMessage(OSMessageAdapter adapter)
  {
    localSendMessage(adapter);
  }

  /**
   *  Description of the Method
   *
   * @param  adapter  Description of Parameter
   */
  public void localSendMessage(MessageAdapter adapter)
  {
    postOffice.processMessage(adapter);
  }

  /**
   *  Description of the Method
   *
   * @param  adapter  Description of Parameter
   */
  public void localSendMessage(OSMessageAdapter adapter)
  {
    postOffice.localSendMessage(adapter);
  }

  /**
   *  Description of the Method
   *
   * @param  message  Description of Parameter
   */
  public void processMessage(MessageAdapter message)
  {
    try
    {
      processMessage((UniversalMessageAdapter) message);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   *  Description of the Method
   *
   * @param  message  Description of Parameter
   */
  public abstract void processMessage(OSMessageAdapter message);

  /**
   *  Description of the Method
   *
   * @param  message  Description of Parameter
   */
  public abstract void processMessage(UniversalMessageAdapter message);
}
