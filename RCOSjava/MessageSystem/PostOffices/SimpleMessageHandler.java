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
 * <DT><B>History:</B><DD>
 * ??/??/96 Fixed bug nolonger registers to postoffice automatically.
 * </DD><DD>
 * 20/05/97 Changed message system.
 * </DD><DD>
 * 21/05/97 Added LocalSendMessage to all posting of a message
 * to the local Post Office instead of broadcasting it everywhere.
 * </DD></DT>
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

  /**
   * Null construct does nothing.
   */
  public SimpleMessageHandler()
  {
  }

  /**
   * Creates a message handler with the given id and regsisters to the given
   * post office.
   *
   * @param newId the id to register with the post office to.
   * @param newPostOffice the created post office to send and receive messages.
   */
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

  /**
   * Create a simple message handler without adding it to a post office.
   */
  public SimpleMessageHandler(String newId)
  {
    id = newId;
  }

  /**
   * Set the id of the message handler.
   */
  public void setId(String newId)
  {
    id = newId;
  }

  /**
   * Returns the id of the message handler.
   */
  public String getId()
  {
    return id;
  }

  /**
   * Send a message to all registered to the post office and forward it to
   * other post offices.
   */
  public abstract void sendMessage(MessageAdapter aMessage);

  /**
   * Send the messages only to the local post office.
   */
  public abstract void localSendMessage(MessageAdapter maMessage);

  /**
   * Process the message that was sent to it.  Called by the post office it
   * was registered with.
   */
  public abstract void processMessage(MessageAdapter maMessage);

  /**
   * If the simple message handler is a post office you register message
   * handlers with it.
   *
   * @param newId name of the message handler.
   * @param newHandler the message handler to add.
   */
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
