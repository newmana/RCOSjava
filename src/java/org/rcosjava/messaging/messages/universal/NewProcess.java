package org.rcosjava.messaging.messages.universal;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.process.ProcessScheduler;

/**
 * Used to send messages about creating new process.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 01/07/1997 Uses Memory </DD>
 * <DD> 03/08/97 Moved to message system </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @created 24th of March 1997
 * @version 1.00 $Date$
 */
public class NewProcess extends UniversalMessageAdapter
{
  /**
   * Description of the Field
   */
  private String fileName;
  /**
   * Description of the Field
   */
  private Memory processMemory;
  /**
   * Description of the Field
   */
  private int fileSize;

  /**
   * Constructor for the NewProcess object
   *
   * @param theSource Description of Parameter
   * @param newFileName Description of Parameter
   * @param newProcessMemory Description of Parameter
   * @param newFileSize Description of Parameter
   */
  public NewProcess(OSMessageHandler theSource, String newFileName,
      Memory newProcessMemory, int newFileSize)
  {
    super(theSource);
    fileName = newFileName;
    processMemory = newProcessMemory;
    fileSize = newFileSize;
  }

  /**
   * Sets the Filename attribute of the NewProcess object
   *
   * @param newFileName The new Filename value
   */
  public void setFileName(String newFileName)
  {
    fileName = newFileName;
  }

  /**
   * Sets the Memory attribute of the NewProcess object
   *
   * @param newMemory The new Memory value
   */
  public void setMemory(Memory newMemory)
  {
    processMemory = newMemory;
  }

  /**
   * Sets the FileSize attribute of the NewProcess object
   *
   * @param newFileSize The new FileSize value
   */
  public void setFileSize(int newFileSize)
  {
    fileSize = newFileSize;
  }

  /**
   * Gets the Filename attribute of the NewProcess object
   *
   * @return The Filename value
   */
  public String getFilename()
  {
    return fileName;
  }

  /**
   * Gets the Memory attribute of the NewProcess object
   *
   * @return The Memory value
   */
  public Memory getMemory()
  {
    return processMemory;
  }

  /**
   * Gets the FileSize attribute of the NewProcess object
   *
   * @return The FileSize value
   */
  public int getFileSize()
  {
    return fileSize;
  }

  /**
   * Gets the StackPages attribute of the NewProcess object
   *
   * @return The StackPages value
   */
  public int getStackPages()
  {
    return 1;
  }

  /**
   * Calculates the number of pages based on the memory block size.
   *
   * @return the number of whole code pages to store the file with.
   */
  public int getCodePages()
  {
    return ((int) (fileSize / Memory.DEFAULT_SEGMENT) + 1);
  }

  /**
   * Executes the newProcess method passing the stored process id.
   *
   * @param theElement the process scheduler to do the work on.
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.newProcess(this);
  }

  /**
   * Returns false so this is not recorded.
   *
   * @return false so this is not recorded.
   */
  public boolean undoableMessage()
  {
    return false;
  }
}
