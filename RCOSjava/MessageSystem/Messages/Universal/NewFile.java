package MessageSystem.Messages.Universal;

import Software.Process.ProgramManager;
import MessageSystem.PostOffices.Animator.AnimatorMessageHandler;
import Software.Kernel.Kernel;
import Hardware.CPU.Interrupt;
import Software.Animator.Process.StartProgram;

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
}