package MessageSystem.PostOffices.OS;

import MessageSystem.PostOffices.PostOffice;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.PostOffices.MessageHandler;
import java.util.Enumeration;

/**
 * Provide message handling centre of operations
 * <P>
 * @author Bruce Jamieson
 * @author Andrew Newman
 * @created 21 October 2000
 */
public class OSOffice extends PostOffice
{
  /**
   *  Constructor for the OSOffice object
   *
   * @param  sMyID  Description of Parameter
   */
  public OSOffice(String newID)
  {
    id = newID;
    // Register ourselves
    //addHandler(sMyID, this);
  }

  /**
   *  Description of the Method
   *
   * @param  aMessage  Description of Parameter
   */
  public void sendMessage(MessageAdapter aMessage)
  {
    //Send to all other registered post offices.
    sendToPostOffices(aMessage);
    //Send to locally registered components.
    localSendMessage(aMessage);
  }

  /**
   *  Description of the Method
   *
   * @param  aMessage  Description of Parameter
   */
  public void sendMessage(UniversalMessageAdapter aMessage)
  {
    sendMessage((MessageAdapter) aMessage);
  }

  /**
   *  Description of the Method
   *
   * @param  aMessage  Description of Parameter
   */
  public void sendMessage(OSMessageAdapter aMessage)
  {
    sendMessage((MessageAdapter) aMessage);
  }

  /**
   *  Description of the Method
   *
   * @param  maMessage  Description of Parameter
   */
  public void localSendMessage(MessageAdapter maMessage)
  {
    if (maMessage.forPostOffice(this))
    {
      //Go through the hashtable returning all the handlers
      //registered.  Send the message to all of them.
      for (Enumeration e = getHandlers().elements(); e.hasMoreElements(); )
      {
        OSMessageHandler theDestination = (OSMessageHandler) e.nextElement();
        //Send the message to the destination
        theDestination.processMessage(maMessage);
      }
    }
  }

  /**
   *  Description of the Method
   *
   * @param  maMessage  Description of Parameter
   */
  public void localSendMessage(OSMessageAdapter maMessage)
  {
    if (maMessage.forPostOffice(this))
    {
      //Go through the hashtable returning all the handlers
      //registered.  Send the message to all of them.
      for (Enumeration e = getHandlers().elements(); e.hasMoreElements(); )
      {
        OSMessageHandler theDestination = (OSMessageHandler) e.nextElement();
        //Send the message to the destination
        theDestination.processMessage(maMessage);
      }
    }
  }

  /**
   *  Description of the Method
   *
   * @param  maMessage  Description of Parameter
   */
  public void sendToPostOffices(MessageAdapter maMessage)
  {
    PostOffice poTmpPostOffice;

    if (!postOffices.isEmpty())
    {
      int iCount;
      for (iCount = 0; iCount < postOffices.size(); iCount++)
      {
        poTmpPostOffice = getPostOffice(iCount);
        if (maMessage.forPostOffice(poTmpPostOffice))
        {
          poTmpPostOffice.localSendMessage(maMessage);
        }
      }
    }
  }

  /**
   *  Description of the Method
   *
   * @param  maMsg  Description of Parameter
   */
  public void processMessage(MessageAdapter maMsg)
  {
    try
    {
      maMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.err.println("Error Processing Message: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
