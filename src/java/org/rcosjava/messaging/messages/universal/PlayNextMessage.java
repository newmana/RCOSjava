package org.rcosjava.messaging.messages.universal;

import org.rcosjava.software.animator.support.ErrorDialog;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.messaging.postoffices.universal.UniversalMessagePlayer;
import org.rcosjava.messaging.postoffices.universal.EndOfMessagesException;

import org.apache.log4j.*;

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
   * Logging class.
   */
  private final static Logger log = Logger.getLogger(PlayNextMessage.class);

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
    try
    {
      theElement.playNextMessage();
    }
    catch (EndOfMessagesException nome)
    {
      new ErrorDialog("Warning - No Messages Left", "You made a request to " +
          "play a message that does not exist.").show();
      log.error("No more messages", nome);
    }
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

