package MessageSystem.Messages.Universal;

import Software.Animator.Process.ProgramManagerAnimator;
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
   * Creates a new file list from a given source.
   *
   * @param theSource the sender of the message
   * @param newFileList the list of files in the directory
   * @param newDirectoryList the list of subdirs.
   */
  public UpdateFileList(OSMessageHandler theSource,
    FIFOQueue newFileList, FIFOQueue newDirectoryList)
  {
    super(theSource);
    fileList = newFileList;
    directoryList = newDirectoryList;
  }

  /**
   * Updates the file list and directory list from the stored items.
   *
   * @param theElement program manager animator that has the methods
   * updateFileList and updateDiretoryList.
   */
  public void doMessage(ProgramManagerAnimator theElement)
  {
    theElement.updateFileList(fileList);
    theElement.updateDirectoryList(directoryList);
  }
}