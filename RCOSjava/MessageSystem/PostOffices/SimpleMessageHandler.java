package MessageSystem.PostOffices;

import MessageSystem.Messages.MessageAdapter;
import MessageSystem.Messages.AddHandler;
import java.util.Enumeration;
import java.util.Hashtable;

//Serialization support
import fr.dyade.koala.serialization.GeneratorInputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Provide sending and receiving facilities for all classes.
 * <P>
 * HISTORY: ??/??/96 Fixed bug nolonger registers to postoffice
 *          automatically.<BR>
 *          20/05/97 Changed message system.<BR>
 *          21/05/97 Added LocalSendMessage to all posting of a message
 *          to the local Post Office instead of broadcasting
 *          it everywhere.<BR>
 * <P>
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

  /**
   * Added for KOML support.  Simply writes the string name of the object
   * for serialization.  Prevents entire graphical/os representation being
   * serialized for each message.
   *
   * @out output stream in which to write these objects to.
   */
  private void writeObject(java.io.ObjectOutputStream out) throws IOException
  {
    //out.writeUTF(id);
    out.writeObject(registeredPostOffice);
  }

  /**
   * Recreates (not instiate) the object.  The object is not properly created
   * and should not be used except to get properties.
   */
  private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    //id = (String) in.readUTF();
    registeredPostOffice = (PostOffice) in.readObject();
  }

  /**
   * Koala XML serialization requirements
   */
  public static void readObject(GeneratorInputStream in)
    throws IOException, ClassNotFoundException
  {
//    in.defaultReadObject();
  }
}
