package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.messaging.postoffices.universal.UniversalMessagePlayer;

/**
 * Tells the universal message player to play another message.
 * <P>
 * @author Andrew Newman.
 * @created 2nd February 2003
 * @version 1.00 $Date$
 */
public class PlayNextMessage extends UniversalMessageAdapter
{
  /**
   * Constructs a new play next message.
   *
   * @param theSource who sent the message.
   */
  public PlayNextMessage(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Call the universal message player.
   *
   * @param theElement the universal message player.
   */
  public void doMessage(UniversalMessagePlayer theElement)
  {
    theElement.playNextMessage();
  }

  /**
   * Returns false so this is not recorded.
   *
   * @return false so this is not recorded.
   */
  public boolean undoableMessage()
  {
    return false;
  }
}

