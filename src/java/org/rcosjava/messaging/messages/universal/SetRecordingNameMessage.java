package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.messaging.postoffices.universal.UniversalMessageRecorder;
import org.rcosjava.messaging.postoffices.universal.UniversalMessagePlayer;

/**
 * Sets the name of the record.  This can either be an existing recording or
 * a new one.  It does not have to exist.  But if it doesn't call
 * CreateRecordingMessage.
 * <P>
 * @author Andrew Newman.
 * @created 2nd February 2003
 * @version 1.00 $Date$
 */
public class SetRecordingNameMessage extends UniversalMessageAdapter
{
  /**
   * The name of the recording.
   */
  private String recordingName;

  /**
   * Constructs the name of the recording.
   *
   * @param theSource who sent the message.
   * @param newRecordingName the name of the recording.
   */
  public SetRecordingNameMessage(AnimatorMessageHandler theSource,
      String newRecordingName)
  {
    super(theSource);
    recordingName = newRecordingName;
  }

  /**
   * Call the universal message player.
   *
   * @param theElement the universal message player.
   */
  public void doMessage(UniversalMessagePlayer theElement)
  {
    theElement.setRecordingName(recordingName);
  }

  /**
   * Call the universal message recorder.
   *
   * @param theElement the universal message recorder.
   */
  public void doMessage(UniversalMessageRecorder theElement)
  {
    theElement.setRecordingName(recordingName);
  }
}