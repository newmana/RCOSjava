package org.rcosjava.software.filesystem;

/**
 * Title:        RCOS
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      UFPE
 * @author Danielly Cruz
 * @version 1.0
 */

public class FileSystemData {

  private int requestID;
  private String returnValue;
  private int[] entriesFileFromFAT;

  public FileSystemData(int newRequestID, String newReturnValue, int[] newEntriesFileFromFAT){
    requestID = newRequestID;
    returnValue = newReturnValue;
    entriesFileFromFAT = newEntriesFileFromFAT;
  }

  public FileSystemData(){
    requestID = -1;
    returnValue = "";
  }

  public int getRequestID(){
    return requestID;
  }

  public String getReturnValue(){
    return returnValue;
  }
  public int[] getEntriesFileFromFAT() {
    return entriesFileFromFAT;
  }
}