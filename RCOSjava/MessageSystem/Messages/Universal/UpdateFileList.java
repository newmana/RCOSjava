//******************************************************/
// FILE     : UpdateFileListMessage
// PURPOSE  : Contains a list of files and subdirectories
//            in the current directory.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/96   Created
//******************************************************/

package MessageSystem.Messages.Universal;

import Software.Animator.Process.ProgramManagerAnimator;
import Software.Process.ProgramManager;
import Software.Util.FIFOQueue;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class UpdateFileList extends UniversalMessageAdapter
{
  private FIFOQueue fifoQFileList;
  private FIFOQueue fifoQDirectoryList;

  public UpdateFileList(OSMessageHandler theSource,
    FIFOQueue newFileList, FIFOQueue newDirectoryList)
  {
    super(theSource);
    this.fifoQFileList = newFileList;
    this.fifoQDirectoryList = newDirectoryList;
  }

  public void doMessage(ProgramManagerAnimator theElement)
  {
    theElement.updateFileList(this.fifoQFileList);
    theElement.updateDirectoryList(this.fifoQDirectoryList);
  }
}

