package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.multimedia.MultimediaAnimator;

/**
 * Indicate to the multimedia animator that there are no more messages left.
 * Should disable the user from being able to press next.
 *
 * @author Andrew Newman.
 * @created 1st July 1996
 * @version 1.00 $Date$
 */
public class NoNextMessage extends UniversalMessageAdapter
{
  /**
   * Create the message sender.
   *
   * @param theSource Description of Parameter
   */
  public NoNextMessage(OSMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Call NoNextMessage of the multimedia animator.
   *
   * @param theElement the multimedia animator to call.
   */
  public void doMessage(MultimediaAnimator theElement)
  {
    theElement.noNextMessage();
  }
}