package org.rcosjava.messaging.messages.os;import org.rcosjava.messaging.postoffices.os.OSMessageHandler;import org.rcosjava.software.ipc.IPC;/** * Message sent to IPC manager to open an existing shared memory structure. <P> * * * <DT> <B>History:</B> * <DD> 08/08/98 Changed to new message system. AN </DD> </DT> <P> * * * * @author Andrew Newman. * @author Bruce Jamieson. * @created 30th of March 1996 * @version 1.00 $Date$ */public class SharedMemoryOpenMessage extends OSMessageAdapter{  /**   * The name of the new shared memory structured.   */  private String sharedMemoryName;  /**   * The process ID opening the shared memory structure.   */  private int PID;  /**   * Constructor for the SharedMemoryOpenMessage object   *   * @param theSource Description of Parameter   * @param newSharedMemoryName Description of Parameter   * @param newPID Description of Parameter   */  public SharedMemoryOpenMessage(OSMessageHandler theSource,      String newSharedMemoryName, int newPID)  {    super(theSource);    sharedMemoryName = newSharedMemoryName;    PID = newPID;  }  /**   * Call sharedMemoryOpen on the IPC.   *   * @param theElement the IPC to call sharedMemoryOpen on.   */  public void doMessage(IPC theElement)  {    theElement.sharedMemoryOpen(sharedMemoryName, PID);  }}