package MessageSystem.Messages.Universal;

import Software.Animator.Process.ProgramManagerAnimator;
import Software.Animator.Multimedia.MultimediaAnimator;
import Software.Process.ProgramManager;
import Software.Util.FIFOQueue;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import java.io.Serializable;

/**
 * Contains a list of files and sub-directories in the current directory.
 * <P>
 * Usage example:
 * <CODE>
 *      UpdateFileList tmpList = new UpdateFileList(this, allFiles,
 *        allDirectories);
 * </CODE>
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th March 1996
 */
 public class UpdateFileList extends UniversalMessageAdapter
{
  /**
   * List of files in the directory requested.
   */
  private FIFOQueue fileList;

  /**
   * List of sub-directories in the directory requested.
   */
  private FIFOQueue directoryList;

  /**
   * The list to retrieve (1 for EXE, 2 for REC)
   */
  private int directoryType;

  /**
   * Creates a new file list from a given source.
   *
   * @param theSource the sender of the message
   * @param newFileList the list of files in the directory
   * @param newDirectoryList the list of subdirs.
   */
  public UpdateFileList(OSMessageHandler theSource,
    FIFOQueue newFileList, FIFOQueue newDirectoryList, int newDirectoryType)
  {
    super(theSource);
    fileList = newFileList;
    directoryList = newDirectoryList;
    directoryType = newDirectoryType;
  }

  /**
   * Updates the file list and directory list from the stored items.
   *
   * @param theElement program manager animator that has the methods
   * updateFileList and updateDiretoryList.
   */
  public void doMessage(ProgramManagerAnimator theElement)
  {
    if (directoryType == 1)
    {
      theElement.updateFileList(fileList);
      theElement.updateDirectoryList(directoryList);
    }
  }

  public void doMessage(MultimediaAnimator theElement)
  {
    if (directoryType == 2)
    {
      theElement.updateDirectoryList(directoryList);
    }
  }

  /**
   * This message returns false, not a message to be recorded or played back.
   */
  public boolean undoableMessage()
  {
    return false;
  }
}