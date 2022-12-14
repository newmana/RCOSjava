package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.software.process.ProgramManager;

/**
 * Creates a new file based on the given filename.
 * <P>
 * @author Andrew Newman.
 * @created 28th March 1996
 * @version 1.00 $Date$
 */
public class NewFile extends UniversalMessageAdapter
{
  /**
   * Description of the Field
   */
  private String filename;

  /**
   * Constructor for the NewFile object
   *
   * @param theSource Description of Parameter
   * @param newFilename Description of Parameter
   */
  public NewFile(AnimatorMessageHandler theSource, String newFilename)
  {
    super(theSource);
    filename = newFilename;
  }

  /**
   * Send a new process interrupt to the Kernel
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProgramManager theElement)
  {
    theElement.newFile(filename);
  }

  /**
   * Set to false so it is not recorded.
   *
   * @return Description of the Returned Value
   */
  public boolean undoableMessage()
  {
    return false;
  }

  /**
   * Sets the Filename attribute of the NewFile object
   *
   * @param newFilename The new Filename value
   */
  private void setFileName(String newFilename)
  {
    filename = newFilename;
  }
}
