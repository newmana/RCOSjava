package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.software.process.ProgramManager;

/**
 * Message sent to program manager to set the thread of executing back to start
 * again to begin executing the program at full speed.
 * <P>
 * @author Andrew Newman.
 * @created 28th of March 1996
 * @version 1.00 $Date$
 */
public class Run extends UniversalMessageAdapter
{
  /**
   * Constructor for the Run object
   *
   * @param theSource who sent the message.
   */
  public Run(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Calls startThread on the Program Manager.
   *
   * @param theElement the object to do work on.
   */
  public void doMessage(ProgramManager theElement)
  {
    theElement.startThread();
  }
}