package MessageSystem.PostOffices;

import MessageSystem.Messages.MessageAdapter;
import MessageSystem.Messages.AddHandler;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.Serializable;

/**
 * Provide sending and receiving facilities for all classes.
 * <P>
 * ??/??/96 Fixed bug nolonger registers to postoffice
 * automatically.<BR>
 * 20/05/97 Changed message system.<BR>
 * 21/05/97 Added LocalSendMessage to all posting of a message
 * to the local Post Office instead of broadcasting
 * it everywhere.<BR>
 *
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @version 1.00 $Date$
 * @created 24th January 1996
 **/
public abstract class SimpleMessageHandler
  implements MessageHandler, Serializable
{
  protected PostOffice registeredPostOffice;
  protected String id;
  private Hashtable registeredHandlers = new Hashtable();

  public SimpleMessageHandler()
  {
  }

  public SimpleMessageHandler(String newId, PostOffice newPostOffice)
  {
    // Save myId and a pointer the PostOffice
    id = newId;
    registeredPostOffice = newPostOffice;
    // Tell the PostOffice that I'm alive
    AddHandler newMessage = new AddHandler(this, newId,
      newPostOffice);
    newPostOffice.processMessage(newMessage);
  }

  public SimpleMessageHandler(String newId)
  {
    id = newId;
  }

  public void setId(String newId)
  {
    id = newId;
  }

  public String getId()
  {
    return id;
  }

  public abstract void sendMessage(MessageAdapter aMessage);
  public abstract void localSendMessage(MessageAdapter maMessage);
  public abstract void processMessage(MessageAdapter maMessage);

  public void addHandler(String newId, MessageHandler newHandler)
  {
    registeredHandlers.put(newId, newHandler);
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

  public void removeHandler(String oldId)
  {
    registeredHandlers.remove(oldId);
  }

  public void clearHandlers()
  {
    registeredHandlers.clear();
  }
}
