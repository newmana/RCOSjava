package org.rcosjava.messaging.postoffices;
import java.util.*;
import org.rcosjava.messaging.messages.MessageAdapter;

/**
 * Provide message handling centre of operations.
 * <P>
 * @author Andrew Newman.
 * @created 21 October 2000
 * @version 1.00 $Date$
 */
public abstract class PostOffice extends SimpleMessageHandler
{
  /**
   * The registered post offices.
   */
  protected ArrayList postOffices = new ArrayList(1);

  /**
   * Constructor for the PostOffice object
   *
   * @param newId the unique identifier of the post office.
   * @param newPostOffice Description of Parameter
   */
  public PostOffice(String newId, PostOffice newPostOffice)
  {
    super(newId, newPostOffice);
  }

  /**
   * Gets the post office held by the index.
   *
   * @param index the index to the vector of post offices.
   * @return the post office.
   */
  public PostOffice getPostOffice(int index)
  {
    return ((PostOffice) postOffices.get(index));
  }

  /**
   * Adds another post office to the registered post offices.
   *
   * @param newPostOffice The post office to add to the vector.
   */
  public void addPostOffice(PostOffice newPostOffice)
  {
    postOffices.add(newPostOffice);
  }

  /**
   * Send message only to other post offices.
   *
   * @param message Message to send to all post offices.
   */
  public abstract void sendToPostOffices(MessageAdapter message);

  /**
   * Send a message to all registered post offices and to all locally registered
   * components.
   *
   * @param message message to send.
   */
  public abstract void sendMessage(MessageAdapter message);

  /**
   * Send a message to only registered objects of local post office.
   *
   * @param message Message to send.
   */
  public abstract void localSendMessage(MessageAdapter message);

  /**
   * Process a sent message.
   *
   * @param message Message received.
   */
  public abstract void processMessage(MessageAdapter message);
}
