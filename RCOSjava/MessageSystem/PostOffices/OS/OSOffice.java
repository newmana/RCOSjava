package MessageSystem.PostOffices.OS;

import MessageSystem.PostOffices.PostOffice;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.PostOffices.MessageHandler;
import java.util.Iterator;
import java.util.SortedMap;

/**
 * Provide message handling centre of operations.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 01/04/98 Modified to use TreeMap.
 * </DD></DT>
 * <P>
 * @author Bruce Jamieson
 * @author Andrew Newman
 * @created 21 October 2000
 */
public class OSOffice extends PostOffice
{
  /**
   * To be done
   *
   * @param newId To be done
   */
  public OSOffice(String newId)
  {
    id = newId;
    // Register ourselves
    //addHandler(sMyID, this);
  }

  public void sendMessage(MessageAdapter message)
  {
    //Send to all other registered post offices.
    sendToPostOffices(message);
    //Send to locally registered components.
    localSendMessage(message);
  }

  /**
   *  Description of the Method
   *
   * @param  message  Description of Parameter
   */
  public void sendMessage(UniversalMessageAdapter message)
  {
    sendMessage((MessageAdapter) message);
  }

  /**
   * To be done
   *
   * @param message To be done
   */
  public void sendMessage(OSMessageAdapter message)
  {
    sendMessage((MessageAdapter) message);
  }

  public void localSendMessage(MessageAdapter message)
  {
    if (message.forPostOffice(this))
    {
      //Go through the hashtable returning all the handlers
      //registered.  Send the message to all of them.

      Iterator tmpIter = this.getHandlers().values().iterator();

      while(tmpIter.hasNext())
      {
        OSMessageHandler theDestination = (OSMessageHandler) tmpIter.next();
        //Send the message to the destination
        theDestination.processMessage(message);
      }
    }
  }

  /**
   * To be done
   *
   * @param message To be done
   */
  public void localSendMessage(OSMessageAdapter message)
  {
    if (message.forPostOffice(this))
    {
      //Go through the hashtable returning all the handlers
      //registered.  Send the message to all of them.

      Iterator tmpIter = this.getHandlers().values().iterator();

      while(tmpIter.hasNext())
      {
        OSMessageHandler theDestination = (OSMessageHandler) tmpIter.next();
        //Send the message to the destination
        theDestination.processMessage(message);
      }
    }
  }

  public void sendToPostOffices(MessageAdapter message)
  {
    PostOffice tmpPostOffice;

    if (!postOffices.isEmpty())
    {
      int count;
      for (count = 0; count < postOffices.size(); count++)
      {
        tmpPostOffice = getPostOffice(count);
        if (message.forPostOffice(tmpPostOffice))
        {
          tmpPostOffice.localSendMessage(message);
        }
      }
    }
  }

  public void processMessage(MessageAdapter message)
  {
    try
    {
      message.doMessage(this);
    }
    catch (Exception e)
    {
      System.err.println("Error Processing Message: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
