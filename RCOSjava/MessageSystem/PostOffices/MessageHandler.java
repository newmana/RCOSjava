package MessageSystem.PostOffices;

import java.util.*;
import MessageSystem.Messages.MessageAdapter;

/**
 * Abstract class to provide base class for message handlers.
 * <P>
 * <B>History:</B><BR>
 * 17/03/96 Modified to include LocalSendMessage to allow broadcasting of
 * messages to just local post office.
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @version 1.00 $Date$
 * @created 24th January 1996
 **/
public interface MessageHandler
{
  /**
   * Set the unique identifier for the message handler.  Post offices will use
   * this to retrieve their names.
   *
   * @param the value of the identifier to set.
   */
  public void setId(String newId);

  /**
   * Returns the unique identifier.
   */
  public String getId();

  /**
   * Sends the message locally within all objects registered to the post office.
   *
   * @param message the message to send to all the objects.
   */
  public void localSendMessage(MessageAdapter message);

  /**
   * Sends the message globally to all post offices and registered objects.
   *
   * @param the message to send to all the objects.
   */
  public void sendMessage(MessageAdapter message);

  /**
   * Adds a handler to receive messages.  Stores the handlers in a hashtable.
   *
   * @param the unqiue identifier of the message handler to add.  Used by the
   *   hashtable as the key.
   * @param the value of the message handler to store in the hashtable.
   */
  public void addHandler(String newId, MessageHandler newHandler);

  /**
   * Returns the message handler given the id of the handler.  Should throw
   * and exception here.
   *
   * @param the name of the handler given that was stored in the hashtable.
   */
  public MessageHandler getHandler(String handlerToGet);

  /**
   * Returns the entire collection of preregistered handlers.
   */
  public Hashtable getHandlers();

  /**
   * Returns the keys (the names) of all the handlers.
   */
  public Enumeration getKeysHandlers();

  /**
   * Removes the handler from the list of handlers based on the unique id.
   * Should throw an exception if not found.
   *
   * @param oldId the existing handler to remove.
   */
  public void removeHandler(String oldId);

  /**
   * Removes all the handlers from the list.
   */
  public void clearHandlers();

  /**
   * The method that is called by the post office when to a registered handler.
   *
   * @param message the message to do.
   */
  public void processMessage(MessageAdapter message);
}