package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.hardware.cpu.Interrupt;

/**
 * Creates a new file based on the given filename.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 28th March 1996
 */
public class NewFile extends UniversalMessageAdapter
{
  private String filename;

  public NewFile(AnimatorMessageHandler theSource, String newFilename)
  {
    super(theSource);
    filename = newFilename;
  }

  private void setFilename(String newFilename)
  {
    filename = newFilename;
  }

  /**
   * Send a new process interrupt to the Kernel
   */
  public void doMessage(ProgramManager theElement)
  {
    theElement.newFile(filename);
  }

  /**
   * Set to false so it is not recorded.
   */
  public boolean undoableMessage()
  {
    return false;
  }
}