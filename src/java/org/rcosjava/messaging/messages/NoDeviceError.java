package org.rcosjava.messaging.messages;
import org.rcosjava.messaging.postoffices.PostOffice;
import org.rcosjava.messaging.postoffices.SimpleMessageHandler;

/**
 * This message is sent when a message is sent to a device that isn't registered
 * with the message handler.
 * <P>
 * @author Andrew Newman
 * @created 24th March 1996
 * @version 1.00 $Date$
 */
public class NoDeviceError extends MessageAdapter
{
  /**
   * The error message to be displayed by the post office.
   */
  private static final String ERROR_MESSAGE = "No such device registered: ";

  /**
   * Creates a message with a known body (should be serializable) and a known
   * source.
   *
   * @param newSource sets the source of the message.
   * @param newBody the message contents.
   */
  public NoDeviceError(SimpleMessageHandler newSource, Object newBody)
  {
    super(newSource, newBody);
  }

  /**
   * Current just prints to the System.err the error message. Should probably
   * throw and exception and/or log the problem. May-be each post office should
   * have a method for handling this.
   *
   * @param theElement the post office to which the error occurred.
   */
  public void doMessage(PostOffice theElement)
  {
    System.err.println("POST OFFICE " + ERROR_MESSAGE + this.getSource().getId() +
        " from " + this.getSource());
  }

  /**
   * The object that sent the message will also print out the error. Again, this
   * is only to System.err and not a proper exception or logging event.
   *
   * @param theElement the message that sent the message.
   */
  public void doMessage(Object theElement)
  {
    System.err.println(ERROR_MESSAGE + theElement.getClass().getName());
  }
}

