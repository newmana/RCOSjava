package org.rcosjava.software.filesystem.msdos.exception;

/**
 * Title:        RCOS
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      UFPE
 * @author Danielly Cruz
 * @version 1.0
 */

public class MSDOSDirectoryException extends Exception{

  String menssageError;

  public MSDOSDirectoryException(String menssageError) {
     super(menssageError);
  }
}