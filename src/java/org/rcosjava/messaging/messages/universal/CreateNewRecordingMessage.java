package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.messaging.postoffices.universal.UniversalMessageRecorder;

/**
 * Creates a new recording.  This resets the current message count and create
 * the recording.
 * <P>
 * @author Andrew Newman.
 * @created 2nd February 2003
 * @version 1.00 $Date$
 */
public class CreateNewRecordingMessage extends UniversalMessageAdapter
{
  /**
   * Constructs a toggle recording message.
   *
   * @param theSource who sent the message.
   */
  public CreateNewRecordingMessage(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Call the universal message recorder.
   *
   * @param theElement the universal message recorder.
   */
  public void doMessage(UniversalMessageRecorder theElement)
  {
    theElement.createNewRecording();
  }
}

