package MessageSystem.Messages.Universal;

import Software.Process.ProgramManager;
import Software.Animator.Process.ProgramManagerAnimator;
import MessageSystem.PostOffices.Animator.AnimatorMessageHandler;
//Serialization support
import java.io.Serializable;

/**
 * Sent when the animator requests a directory listing.
 *
 * Usage example:
 * <CODE>
 *      UpdateList tmpList = new UpdateList(this, "/mnt/tool/java/pcode");
 * </CODE>
 *
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 28th March 1997
 */
public class UpdateList extends UniversalMessageAdapter
//  implements Serializable
{
  /**
   * The directory to list.
   */
  private String directory;

  /**
   * Constructs a update list request.
   *
   * @param theSource of that the request comes from.
   * @param newDirectory string representation of a directory.
   */
  public UpdateList(AnimatorMessageHandler theSource,
    String newDirectory)
  {
    super(theSource);
    directory = newDirectory;
  }

  /**
   * Sets a new directory given that the object is already created successfully.
   *
   * @param newDirectory change the directory to.
   */
  public void setDirectory(String newDirectory)
  {
    directory = newDirectory;
  }

  /**
   * The destination is the a program manager.  Calls updateList on it.
   *
   * @param theElement the program manager object to do the work on.
   */
  public void doMessage(ProgramManager theElement)
  {
    theElement.updateList(directory);
  }
}

