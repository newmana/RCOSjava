package net.sourceforge.rcosjava.messaging.messages;

import net.sourceforge.rcosjava.messaging.postoffices.SimpleMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.PostOffice;

/**
 * A simple method that does not override any of the messages default
 * properties.  Post offices receiving this message knows that the sender
 * of the message wishes to register with it.
 * <P>
 * @author Andrew Newman
 * @version 1.00 $Date$
 * @created 24th March 1996
 */
public class RegisterDevice extends MessageAdapter
{
  /**
   * Creates a message with a known body (should be serializable) and a known
   * source.
   *
   * @param newSource sets the source of the message.
   * @param newBody the message contents.
   */
  public RegisterDevice(SimpleMessageHandler newSource, Object newBody)
  {
    super(newSource, newBody);
  }
}

