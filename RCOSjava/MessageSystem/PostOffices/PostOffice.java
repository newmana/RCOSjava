package MessageSystem.PostOffices;

import java.util.Enumeration;
import java.util.Vector;
import MessageSystem.Messages.MessageAdapter;

/**
 * Provide message handling centre of operations.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 21 October 2000
 */
public abstract class PostOffice extends SimpleMessageHandler
{
  /**
   * The registered post offices.
   */
  protected Vector postOffices = new Vector(1, 1);

  /**
   * The type of post office (name of the class).
   */
  protected String type;

  /**
   * Constructor for the PostOffice object
   */
  public PostOffice()
  {
  }

  /**
   * Constructor for the PostOffice object
   *
   * @param newId the unique identifier of the post office.
   */
  public PostOffice(String newId)
  {
    id = newId;
    type = this.getClass().getName();
    //Add myself so that I can get registration messages/etc.
    addHandler(getId(), this);
  }

  /**
   * Gets the post office held in the vector.
   *
   * @param index the index to the vector of post offices.
   * @return the post office.
   */
  public PostOffice getPostOffice(int index)
  {
    return ((PostOffice) postOffices.elementAt(index));
  }

  /**
   * Adds a post office to the vector.
   *
   * @param newPostOffice The post office to add to the vector.
   */
  public void addPostOffice(PostOffice newPostOffice)
  {
    postOffices.addElement(newPostOffice);
  }

  /**
   * Send message globally.
   *
   * @param message Message to send to all post offices.
   */
  public abstract void sendToPostOffices(MessageAdapter message);

  /**
   * Send a message to all registered post offices and to all locally registered
   * components.
   *
   * @param the message to send to the post office and components.
   */
  public abstract void sendMessage(MessageAdapter message);

  /**
   * Send a message to only registered object of local post office.
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
