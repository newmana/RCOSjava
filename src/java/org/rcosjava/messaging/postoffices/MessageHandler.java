package org.rcosjava.messaging.postoffices;

import java.util.*;
import org.rcosjava.messaging.messages.MessageAdapter;

/**
 * Abstract class to provide base class for message handlers.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 17/03/96 Modified to include LocalSendMessage to allow broadcasting of
 * messages to just local post office. </DD>
 * <DD> 01/04/98 Modified to use TreeMap. </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @created 24th January 1996
 * @version 1.00 $Date$
 */
public interface MessageHandler extends Comparable
{
  /**
   * Set the unique identifier for the message handler. Post offices will use
   * this to retrieve their names.
   *
   * @param newId The new Id value
   */
  public void setId(String newId);

  /**
   * Returns the unique identifier.
   *
   * @return The Id value
   */
  public String getId();

  /**
   * Sends the message locally within all objects registered to the post office.
   *
   * @param message the message to send to all the objects.
   */
  public void localSendMessage(MessageAdapter message);

  /**
   * Sends the message globally to all message handlers.
   *
   * @param message Description of Parameter
   */
  public void sendMessage(MessageAdapter message);

  /**
   * Adds a handler to receive messages. Stores the handlers in a hashtable.
   *
   * @param newId The feature to be added to the Handler attribute
   * @param newHandler The feature to be added to the Handler attribute
   */
  public void addHandler(String newId, MessageHandler newHandler);

  /**
   * Returns the message handler given the id of the handler. Should throw and
   * exception here.
   *
   * @param handlerToGet Description of Parameter
   * @return The Handler value
   */
  public MessageHandler getHandler(String handlerToGet);

  /**
   * Returns the entire collection of preregistered handlers.
   *
   * @return the handlers.
   */
  public TreeMap getHandlers();

  /**
   * Returns the keys (the names) of all the handlers.
   *
   * @return The KeysHandlers value
   */
  public Iterator getKeysHandlers();

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
   * The method that is called by the post office to a registered handler. This
   * is for PostOffice based message only (those that extend MessageAdapter).
   *
   * @param message the message to do.
   */
  public void processMessage(MessageAdapter message);
}
