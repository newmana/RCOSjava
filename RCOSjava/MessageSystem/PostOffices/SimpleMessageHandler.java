//***************************************************************************
// FILE     : SimpleMessageHandler.java
// PACKAGE  : MessageSystem
// PURPOSE  : Provide sending and receiving facilities for all classes.
// AUTHOR   : Bruce Jamieson
// MODIFIED : Andrew Newman
// HISTORY  : 24/01/96 Created
//            ??/??/96 Fixed bug nolonger registers to postoffice
//                     automatically.
//            20/05/97 Changed message system
//            21/05/97 Added LocalSendMessage to all posting of a message
//                     to the local Post Office instead of broadcasting
//                     it everywhere.
//
//***************************************************************************/

package MessageSystem.PostOffices;

import MessageSystem.Messages.MessageAdapter;
import MessageSystem.Messages.AddHandler;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.Serializable;

public abstract class SimpleMessageHandler implements MessageHandler, Serializable
{
  protected PostOffice registeredPostOffice;
  protected String id;
  private Hashtable registeredHandlers = new Hashtable();

  public SimpleMessageHandler()
  {
  }

  public SimpleMessageHandler(String newID, PostOffice newPostOffice)
  {
    // Save myId and a pointer the PostOffice
    id = newID;
    registeredPostOffice = newPostOffice;
    // Tell the PostOffice that I'm alive
    AddHandler newMessage = new AddHandler(this, newID,
      newPostOffice);
    newPostOffice.processMessage(newMessage);
  }

  public SimpleMessageHandler(String newID)
  {
    id = newID;
  }

  public void setID(String newID)
  {
    id = newID;
  }

  public String getID()
  {
    return id;
  }

  public abstract void sendMessage(MessageAdapter aMessage);
  public abstract void localSendMessage(MessageAdapter maMessage);
  public abstract void processMessage(MessageAdapter maMessage);

  public void addHandler(String newID, MessageHandler newHandler)
  {
    registeredHandlers.put(newID, newHandler);
  }

  public MessageHandler getHandler(String handlerToGet)
  {
    //System.out.println("Attempting to get handler " + sHandlerToGet + ":" + htHandlers.get(sHandlerToGet));
    return ((MessageHandler) registeredHandlers.get(handlerToGet));
  }

  public Hashtable getHandlers()
  {
    return(registeredHandlers);
  }

  public Enumeration getKeysHandlers()
  {
    return registeredHandlers.keys();
  }

  public void removeHandler(String oldID)
  {
    registeredHandlers.remove(oldID);
  }

  public void clearHandlers()
  {
    registeredHandlers.clear();
  }
}
