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
   * The name of the file to create the process from.
   */
  private String fileName;

  /**
   * The memory of the new process.
   */
  private Memory processMemory;

  /**
   * The size of the file of the program data.
   */
  private long fileSize;

  /**
   * Creates a new NewProcess message object.
   *
   * @param theSource the sender of the message.
   * @param newFileName the filename of the new process.
   * @param newProcessMemory the contents of the file.
   * @param newFileSize the size of the file.
   */
  public NewProcess(OSMessageHandler theSource, String newFileName,
      Memory newProcessMemory, long newFileSize)
  {
    super(theSource);
    fileName = newFileName;
    processMemory = newProcessMemory;
    fileSize = newFileSize;
  }

  /**
   * Sets the value of the file name attribute.
   *
   * @param newFileName The new value for file name.
   */
  public void setFileName(String newFileName)
  {
    fileName = newFileName;
  }

  /**
   * Sets the value of the memory attribute.
   *
   * @param newMemory The new value for memory.
   */
  public void setMemory(Memory newMemory)
  {
    processMemory = newMemory;
  }

  /**
   * Sets the value of the file size attribute.
   *
   * @param newFileSize The new value for the file size.
   */
  public void setFileSize(int newFileSize)
  {
    fileSize = newFileSize;
  }

  /**
   * Returns the file name attribute.
   *
   * @return the file name attribute.
   */
  public String getFileName()
  {
    return fileName;
  }

  /**
   * Returns the memory attribute.
   *
   * @return the memory attribute.
   */
  public Memory getMemory()
  {
    return processMemory;
  }

  /**
   * Returns the size of the file.
   *
   * @return the size of the file.
   */
  public long getFileSize()
  {
    return fileSize;
  }

  /**
   * Returns the number of stack pages to allocate the program.
   *
   * @return the number of stack pages to allocate the program.
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
