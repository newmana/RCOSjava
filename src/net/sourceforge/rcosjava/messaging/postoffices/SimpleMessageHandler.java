package net.sourceforge.rcosjava.messaging.postoffices;

//Serialization support
import fr.dyade.koala.serialization.GeneratorInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeMap;
import net.sourceforge.rcosjava.messaging.messages.AddHandler;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.software.util.PriorityQueue;

/**
 * Provide sending and receiving facilities for all classes. <P>
 *
 *
 * <DT> <B>History:</B>
 * <DD> ??/??/1996 Fixed bug nolonger registers to postoffice automatically.
 * </DD>
 * <DD> 20/05/97 Changed message system. </DD>
 * <DD> 21/05/1997 Added LocalSendMessage to all posting of a message to the
 * local Post Office instead of broadcasting it everywhere. </DD>
 * <DD> 01/04/2001 Changed to using TreeMap for handler storage. Implemented
 * compare and equals. </DD> </DT>
 *
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @created 24th January 1996
 * @version 1.00 $Date$
 */
public abstract class SimpleMessageHandler
     implements MessageHandler, Serializable
{
  /**
   * Description of the Field
   */
  protected PostOffice registeredPostOffice;
  /**
   * Description of the Field
   */
  protected String id;
  /**
   * Description of the Field
   */
  private TreeMap registeredHandlers = new TreeMap();

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
    if (newPostOffice != null)
    {
      AddHandler newMessage = new AddHandler(this, newId, newPostOffice);
      newPostOffice.processMessage(newMessage);
    }
  }

  /**
   * Create a simple message handler without adding it to a post office.
   *
   * @param newId Description of Parameter
   */
  public SimpleMessageHandler(String newId)
  {
    id = newId;
  }

  /**
   * Koala XML serialization requirements
   *
   * @param in Description of Parameter
   * @exception IOException Description of Exception
   * @exception ClassNotFoundException Description of Exception
   */
  public static void readObject(GeneratorInputStream in)
    throws IOException, ClassNotFoundException
  {
//    in.defaultReadObject();
  }

  /**
   * Set the id of the message handler.
   *
   * @param newId The new Id value
   */
  public void setId(String newId)
  {
    id = newId;
  }

  /**
   * Returns the id of the message handler.
   *
   * @return the id of the message handler.
   */
  public String getId()
  {
    return id;
  }

  /**
   * Searchs the currently registered handlers for the one registered with a
   * given string value.
   *
   * @param handlerToGet the unique identifier of the registered object.
   * @return the message handler object related to the name given.
   */
  public synchronized MessageHandler getHandler(String handlerToGet)
  {
    return ((MessageHandler) registeredHandlers.get(handlerToGet));
  }

  /**
   * @return a copy of all the tree handlers.
   */
  public synchronized TreeMap getHandlers()
  {
    return ((TreeMap) registeredHandlers.clone());
  }

  /**
   * @return an iterator containing all the keys (as string values).
   */
  public synchronized Iterator getKeysHandlers()
  {
    return registeredHandlers.keySet().iterator();
  }

  /**
   * Send a message to all registered to the post office and forward it to other
   * post offices.
   *
   * @param message Description of Parameter
   */
  public abstract void sendMessage(MessageAdapter message);

  /**
   * Send the messages only to the local post office.
   *
   * @param message Description of Parameter
   */
  public abstract void localSendMessage(MessageAdapter message);

  /**
   * Process the message that was sent to it. Called by the post office it was
   * registered with.
   *
   * @param message Description of Parameter
   */
  public abstract void processMessage(MessageAdapter message);

  /**
   * If the simple message handler is a post office you register message
   * handlers with it. This will register the message handler with the post
   * office (or other class). To remove the handler call removeHandler.
   *
   * @param newId name of the message handler.
   * @param newHandler the message handler to add.
   */
  public synchronized void addHandler(String newId, MessageHandler newHandler)
  {
    registeredHandlers.put(newId, newHandler);
  }

  /**
   * If the simple message handler is a post office you register message
   * handlers with it. This will deregister the message handler from the post
   * office.
   *
   * @param oldId Description of Parameter
   */
  public synchronized void removeHandler(String oldId)
  {
    registeredHandlers.remove(oldId);
  }

  /**
   * This is remove all handlers registered with the class.
   */
  public synchronized void clearHandlers()
  {
    registeredHandlers.clear();
  }

  /**
   * A SimpleMessageHandler is equal if the handler has the same id property.
   *
   * @param obj the object to compare this one with.
   * @return if the objects are equal.
   */
  public boolean equals(Object obj)
  {
    if (obj != null && (obj.getClass().equals(this.getClass())))
    {
      SimpleMessageHandler handler = (SimpleMessageHandler) obj;

      if (handler.getId().equals(this.getId()))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * This allows comparison of the handlers based on the id property of the
   * handlers only. It does not compare the contents of the registered handler
   * or the post office that it's registered to.
   *
   * @param obj1 the object to compare to.
   * @param obj2 the object to compare with.
   * @return the comparison of the two strings.
   * @throws ClassCastException if either object is not of the correct type.
   */
  public int compare(Object obj1, Object obj2)
    throws ClassCastException
  {
    if (obj1.getClass().equals(this.getClass()) &&
        obj2.getClass().equals(this.getClass()))
    {
      SimpleMessageHandler handler1 = (SimpleMessageHandler) obj1;
      SimpleMessageHandler handler2 = (SimpleMessageHandler) obj2;

      return handler1.getId().compareTo(handler2.getId());
    }
    throw new ClassCastException();
  }

  /**
   * Added for KOML support. Simply writes the string name of the object for
   * serialization. Prevents entire graphical/os representation being serialized
   * for each message.
   *
   * @param out Description of Parameter
   * @exception IOException Description of Exception
   * @out output stream in which to write these objects to.
   */
  private void writeObject(java.io.ObjectOutputStream out)
    throws IOException
  {
    //out.writeUTF(id);
    out.writeObject(registeredPostOffice);
  }

  /**
   * Recreates (not instiate) the object. The object is not properly created and
   * should not be used except to get properties.
   *
   * @param in Description of Parameter
   * @exception IOException Description of Exception
   * @exception ClassNotFoundException Description of Exception
   */
  private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    //id = (String) in.readUTF();
    registeredPostOffice = (PostOffice) in.readObject();
  }
}
