//***********************************************************************
// FILE     : IPC.java
// PURPOSE  : Provide Shared memory and semaphore facilities
//		        to the system.
// AUTHOR   : Bruce Jamieson
// MODIFIED : Andrew Newman
// HISTORY  : 30/03/96 Completed w/o MMU page requests. BJ
//            08/10/98 Rewrote memory handling (sem and shr mem). AN
// TO DO    : Need to allocate Shared Memory and Semaphore using
//            main memory instead of internal structures.
//***********************************************************************

package Software.IPC;

import java.lang.String;
import java.util.Hashtable;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import MessageSystem.PostOffices.OS.OSOffice;
import MessageSystem.PostOffices.MessageHandler;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.OS.ShrmInit;
import MessageSystem.Messages.OS.ShrmRet;
import MessageSystem.Messages.OS.ShrmSize;
import MessageSystem.Messages.OS.ShrmRead;
import MessageSystem.Messages.OS.ShrmWrite;
import MessageSystem.Messages.OS.ReturnValue;
import MessageSystem.Messages.Universal.BlockedToReady;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import Hardware.Memory.Memory;
import Software.Util.SemaphoreQueue;

public class IPC extends OSMessageHandler
{
  private SemaphoreQueue sqSemaphoreTable = new SemaphoreQueue(10, 10);
  private int iSemaphoreCount = 0;
  // Two hashtables with different indexes - no restrictions
  // on number of segments etc.
  private Hashtable hSharedMemoryTable = new Hashtable();
  private Hashtable hSharedMemoryIDTable = new Hashtable();
  private int iSharedMemCount = 0;
	private static final String MESSENGING_ID = "IPC";
 
  public IPC(OSOffice aPostOffice)
  {
    super(MESSENGING_ID, aPostOffice);
  }

  public void semaphoreCreate(String sSemaphoreName, int iPID, int iInitValue)
  {
    System.out.println("IPC: Creating Semaphore: " + sSemaphoreName);
    if (sqSemaphoreTable.isMember(sSemaphoreName))
    {
      // This means that someone has already created this 
      // semaphore - we have got to nicely reply "no"
	    // Use -1 SemID
      //ReturnValueMessage aMessage = new ReturnValueMessage(this, -1);
      //sendMessage(aMessage);
    }
    else
    {
      iSemaphoreCount++;
      // We give this nice process a semaphore
      Semaphore semNewSemaphore = new Semaphore(sSemaphoreName,
        iSemaphoreCount, iPID, iInitValue);      
      sqSemaphoreTable.insert(semNewSemaphore);
      //Return the integer value (SemID) of the semaphore created.
      //ReturnValueMessage aMessage = new ReturnValueMessage(this, (short) iSemaphoreCount);
      //sendMessage(aMessage);
      //aMessage = new SemaphoreCreatedMessage(this, iSemaphoreCount);
      //sendMessage(aMessage);       
      // End of story - consider the semaphore created and
      // connected to... (Value must be > 0 else probs)
    }    
  }

  public void semaphoreOpen(String sSemaphoreName, int iPID)
  {
      System.out.println("IPC: Open Semaphore: " + sSemaphoreName);
      if (sqSemaphoreTable.isMember(sSemaphoreName))
      {
        // Check that the semaphore exists - it does
        // Open Semaphore
        Semaphore semExistingSemaphore = (Semaphore) 
          sqSemaphoreTable.peek(sSemaphoreName);
        semExistingSemaphore.open(iPID);
        int semID = semExistingSemaphore.getID();
        // Done.  Now we return a message
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, (short) iSemaphoreCount);
        //sendMessage(aMessage);
        //SemaphoreOpenedMessage aMessage = new SemaphoreOpenedMessage(this, iSemaphoreCount);
        //sendMessage(aMessage);
      }
      else
      {
        // The semaphore does not exist (someone trying to open
        // a "non created" semaphore)
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, -1);
        //sendMessage(aMessage);
      }      
  }

  public void sempahoreClose(int iSemaphoreID, int iPID)
  {
      System.out.println( "IPC - Closing Semaphore: " + iSemaphoreID);
      if (sqSemaphoreTable.isMember(iSemaphoreID))
      {
        // Check that the semaphore exists - it does
        // Now Close it
        Semaphore semExistingSemaphore = (Semaphore) 
          sqSemaphoreTable.peek(iSemaphoreID);
        int iNoConnectedProcesses = semExistingSemaphore.close(iPID);
        if (iNoConnectedProcesses == 0)
        {
          // Ok - That was the last connected PID
          // Remove sempahore
          sqSemaphoreTable.getSemaphore(iSemaphoreID);
        }        
        // Default Message Back
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, (short) iSemaphoreID);
        //sendMessage(aMessage);
        //SemaphoreClosedMessage aMessage = new SemaphoreClosedMessage(this, iSemaphoreID);
        //sendMessage(aMessage);
      }
      else
      {
        // Some mistake?  Sem does NOT exist?
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, -1);
        //sendMessage(aMessage);
      }  
  }

  public void sempahoreSignal(int iSemaphoreID, int iPID)
  {
      System.out.println( "IPC - Semaphore Signal: " + iSemaphoreID);
      if (sqSemaphoreTable.isMember(iSemaphoreID))
      {
        // The semaphore exists - now issue a signal
        Semaphore semExistingSemaphore = (Semaphore) 
          sqSemaphoreTable.peek(iSemaphoreID);
        int iProcessID = semExistingSemaphore.signal();
        
        // Send the reply 	
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, (short) iProcessID);
        //sendMessage(aMessage);
        //SemaphoreSignalledMessage aMessage = new SemaphoreSignalledMessage(this, iProcessID);
        //sendMessage(aMessage);
  
        // NOW - if process id wasn't -1, then we have to wake
        // the process up.
        if (iProcessID != -1)
        {
          //blockedToReadyMessage aMessage = new BlockedToReadyMessage(this, iProcessID);
          //sendMessage(aMessage);
        }
      }
      else
      {
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, -1);
        //sendMessage(aMessage);
      }
  }

  public void semaphoreWait(int iSemaphoreID, int iPID)
  {
      System.out.println( "IPC - Semaphore Wait: " + iSemaphoreID);
      if (sqSemaphoreTable.isMember(iSemaphoreID))
      {
        // The semaphore exists - now do a wait on it
        Semaphore semExistingSemaphore = (Semaphore) 
          sqSemaphoreTable.peek(iSemaphoreID);
        int iValue = semExistingSemaphore.wait(iPID);        
        // Send the default response
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, (short) iSemaphoreID);
        //sendMessage(aMessage);
        //aMessage = new MessageAdapter(this, iSemaphoreID);
        //sendMessage(aMessage);
        // NOW - if the value was -1, then we have to block
        // the process
        if (iValue == -1)
        {
          //BlockCurrentProcessMessage aMessage = new BlockCurrentProcessMessage(this);
          //sendMessage(aMessage);
        }
      }
      else
      {
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, -1);
        //sendMessage(aMessage);
      }
  }

  public void sharedMemoryCreate(String sSharedMemoryName, int iPID,
    int iSize)
  {
    if (hSharedMemoryTable.containsKey(sSharedMemoryName))
    {
      // This means that someone has already created this 
      // shrm block (on that id) - nicely say no.
      //ReturnValueMessage aMessage = new ReturnValueMessage(this, -1);
      //sendMessage(aMessage);
    }
    else
    {
      iSharedMemCount++;      
      // We give this nice process some shared memory ;-)
      SharedMemory shShrm = new SharedMemory(sSharedMemoryName, 
        iSharedMemCount, iPID, iSize);      
      hSharedMemoryTable.put(sSharedMemoryName, shShrm);
      Integer iShrmID = new Integer(iSharedMemCount);
      hSharedMemoryIDTable.put(iShrmID, shShrm);              
      // Two Tables - 1 indexed by the String shrm, one by
      // the simpleint shrm number.       
      //aMessage = new ReturnValueMessage(this, (short) iSharedMemCount);
      //sendMessage(aMessage);
      //aMessage = new SharedMemoryCreatedMessage(this, iSharedMemCount, 
      //  iPID, iSize);
      //sendMessage(aMessage);      
    }
  }
  
  public void sharedMemoryOpen(String sSharedMemoryName, int iPID)
  {
    if (hSharedMemoryTable.containsKey(sSharedMemoryName))
    {
      // Check that the shrm exists - it does
      // Open shrm
      SharedMemory shShrm = (SharedMemory) 
        hSharedMemoryTable.get(sSharedMemoryName);
      shShrm.open(iPID);
      int iSharedMemID = shShrm.getShrmID();
      // Done.  Now we return a message
      //aMessage = new ReturnValueMessage(this, (short) iSharedMemID);
      //sendMessage(aMessage);
      //aMessage = new SharedMemoryOpenedMessage(this, iSharedMemID, iPID);
      //sendMessage(aMessage);      
    }
    else
    {
      // The shrm does not exist (someone trying to open
      // a "non created" shrm segment)
      //aMessage = new ReturnValueMessage(this, -1);
      //sendMessage(aMessage);
    }
  }

  public void sharedMemoryClose(int iSharedMemoryID, int iPID)
  {
    Integer iShrmID = new Integer(iSharedMemoryID);
    if (hSharedMemoryIDTable.containsKey(iShrmID))
    {
        // Check that the semaphore exists - it does
        // Now Close it
        SharedMemory shShrm = (SharedMemory) hSharedMemoryIDTable.get(iShrmID);
        int iNoConnections = shShrm.close(iPID);
        if (iNoConnections == 0)
        {
          // Ok - That was the last connected PID - kill the semaphore          
          String sSemaphoreName = shShrm.getStrID();
          hSharedMemoryTable.remove(sSemaphoreName);
          hSharedMemoryIDTable.remove(iShrmID);
          // Assume other handle this when the receive the MemoryClosed
          // successful message.
        }        
        // Was successful return 0 to Kernel.
        //aMessage = new ReturnValueMessage(this, 0);
        //sendMessage(aMessage);
        // Let everyone else know.
        //aMessage = new SharedMemoryClosedMessage(this,
        //  iSharedMemoryID, iPID);
        //sendMessage(aMessage); 
      }
      else
      {
        // Some mistake?  Shrm does NOT exist?
        //aMessage = new ReturnValueMessage(this, -1);
        //sendMessage(aMessage);
      }
  }

  public void sharedMemoryRead(int iSharedMemoryID, int iOffset)
  {      
    Integer iShrmID = new Integer(iSharedMemoryID);
    if (hSharedMemoryIDTable.containsKey(iShrmID))
    {
      // Check that the shrm exists - it does
      SharedMemory shShrm = (SharedMemory) hSharedMemoryIDTable.get(iShrmID);        
      short sData = shShrm.read(iOffset);        
      // Return result to Kernel
      //aMessage = new ReturnValueMessage(this, sData);
      //sendMessage(aMessage);
      // Was it a success?
      if(sData != -1)
      {        
        // Was successful let everyone else know.
        //aMessage = new SharedMemoryReadedMessage(this,
        //  iSharedMemoryID);
        //sendMessage(aMessage);
      }
    }
    else
    {
      // No such Shrm
      //aMessage = new ReturnValueMessage(this, -1);
      //sendMessage(aMessage);
    }
  }

  public void sharedMemoryWrite(int iSharedMemoryID, int iOffset, short sNewValue)
  {
    Integer iShrmID = new Integer(iSharedMemoryID);
    if (hSharedMemoryIDTable.containsKey(iShrmID))
    {
        // Check that the shrm exists - it does
        SharedMemory shShrm = (SharedMemory) hSharedMemoryIDTable.get(iShrmID);
        short sResult = shShrm.write(iOffset, sNewValue);
        // Let kernel know the result
        //aMessage = new ReturnValueMessage(this, sResult);
        //sendMessage(aMessage);
        // Was it a success?
        if(sResult != -1)
        {        
          // Then let others know
          //aMessage = new MessageAdapter(this, iSharedMemoryID);                                                  
          //sendMessage(aMessage);
        }
    }
    else
    {
      // No such Shrm?
      //aMessage = new ReturnValueMessage(this, -1);
      //sendMessage(aMessage);
    }
  }

  public void sharedMemorySize(int iSharedMemoryID)
  {
    Integer iShrmID = new Integer(iSharedMemoryID);
    if (hSharedMemoryIDTable.containsKey(iShrmID))
    {
      // Check that the shrm exists - it does
      //SharedMemory shShrm = (SharedMemory) 
      //  hSharedMemoryIDTable.get(iSharedMemoryID);
      //int iSize = shShrm.size();
      // Let kernel know the result
      //aMessage = new ReturnValueMessage(this, (short) iSize);
      //sendMessage(aMessage);
      //if (iSize != -1)
      //{
        //aMessage = new SharedMemorySizeMessage(this,
        //  iSharedMemoryID);        
        //sendMessage(aMessage);      
      //}
    }
    else
    {
      // Don't know about this segment?
      //aMessage = new ReturnValueMessage(this, -1);
      //sendMessage(aMessage);
    }      
  }

  public void processMessage(OSMessageAdapter aMessage)
  {
    try
    {          
      aMessage.doMessage(this);        
    }
    catch (Exception e)
    {
      System.out.println("Error processing message: "+e);
      e.printStackTrace();
    }  
  }

	public void processMessage(UniversalMessageAdapter aMessage)
  {
    try
    {          
      aMessage.doMessage(this);        
    }
    catch (Exception e)
    {
      System.out.println("Error processing message: "+e);
      e.printStackTrace();
    }  
  }
}
