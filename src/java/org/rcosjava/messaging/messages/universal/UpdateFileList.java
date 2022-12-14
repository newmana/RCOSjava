package org.rcosjava.messaging.messages.universal;
import java.io.Serializable;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.multimedia.MultimediaAnimator;
import org.rcosjava.software.animator.process.ProgramManagerAnimator;
import org.rcosjava.software.util.FIFOQueue;

/**
 * Contains a list of files and sub-directories in the current directory.
 * <P>
 * Usage example: <CODE>
 *      UpdateFileList tmpList = new UpdateFileList(this, allFiles,
 *        allDirectories);
 * </CODE>
 * <P>
 * @author Andrew Newman.
 * @created 24th March 1996
 * @version 1.00 $Date$
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
   * @param newDirectoryType Description of Parameter
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
   *      updateFileList and updateDiretoryList.
   */
  public void doMessage(ProgramManagerAnimator theElement)
  {
    if (directoryType == 1)
    {
      theElement.updateFileList(fileList);
      theElement.updateDirectoryList(directoryList);
    }
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(MultimediaAnimator theElement)
  {
    if (directoryType == 2)
    {
      theElement.updateDirectoryList(directoryList);
    }
  }

  /**
   * This message returns false, not a message to be recorded or played back.
   *
   * @return Description of the Returned Value
   */
  public boolean undoableMessage()
  {
    return false;
  }
}
