package net.sourceforge.rcosjava.messaging.postoffices.os;

import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.universal.UniversalMessageRecorder;

/**
 * Recording facilities for all operating system messages.
 * <P>
 * @see net.sourceforge.rcosjava.messaging.postoffices.universal.UniversalMessageRecorder
 * @author Andrew Newman
 * @created 25th February 2001
 * @version 1.00 $Date$
 */
public class OSMessageRecorder extends OSMessageHandler
{
  /**
   * The recorder object that all messages are sent.
   */
  private UniversalMessageRecorder recorder;

  /**
   * Null constructor.  Does nothing.
   */
  public OSMessageRecorder()
  {
  }

  /**
   * Constructs a message recorder to listen to all the message of a particular
   * post office.
   *
   * @param newId the string identifier to register as.
   * @param newPostOffice the post office to register to.
   */
  public OSMessageRecorder(String newId, OSOffice newPostOffice,
    UniversalMessageRecorder newRecorder)
  {
    super(newId, newPostOffice);
    recorder = newRecorder;
  }

  /**
   * Processes any and all message of operating system message type (operating
   * system component to operating system component in this case).
   *
   * @param newMessage the message to accept.
   */
  public void processMessage(OSMessageAdapter newMessage)
  {
    if (newMessage.undoableMessage())
      recorder.processOSMessage(newMessage);
  }

  /**
   * Accept the universal messages sent to the post office.  This message
   * can come from either an animator or operating system component.
   *
   * @param newMessage the message to accept.
   */
  public void processMessage(UniversalMessageAdapter newMessage)
  {
    if (newMessage.undoableMessage())
      recorder.processOSUniversalMessage(newMessage);
  }
}
