package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.messaging.postoffices.universal.UniversalMessageRecorder;

/**
 * Toggles the recording of messages on or off.
 * <P>
 * @author Andrew Newman.
 * @created 2nd February 2003
 * @version 1.00 $Date$
 */
public class ToggleRecordingMessage extends UniversalMessageAdapter
{
  /**
   * Constructs a toggle recording message.
   *
   * @param theSource who sent the message.
   */
  public ToggleRecordingMessage(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Call the universal message player.
   *
   * @param theElement the universal message player.
   */
  public void doMessage(UniversalMessageRecorder theElement)
  {
    theElement.toggleRecording();
  }
}

