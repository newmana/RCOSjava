// ************************************************************************//
// FILE     : FileReturnData.java
// PACKAGE  : FileSystem
// PURPOSE  : A container for data being passed to the ProcessScheduler
//            containg the return information
// AUTHOR   : Brett Carter
// HISTORY  : 28/3/96 Created.
//
// ************************************************************************//

package net.sourceforge.rcosjava.messaging.messages.os;


public class FileReturnData
{
  public int PID;
  public int ReturnValue;


  public FileReturnData( int aPID, int aReturnValue)
  {

    PID = aPID;
    ReturnValue = aReturnValue;
  }

  public FileReturnData( )
  {
    PID = -1;
    ReturnValue = -1;
  }





}


