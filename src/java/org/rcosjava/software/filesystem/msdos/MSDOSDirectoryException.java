package org.rcosjava.software.filesystem.msdos;

import org.rcosjava.software.filesystem.DirectoryException;

/**
 * Title: RCOS Description: Copyright: Copyright (c) 2002 Company: UFPE
 *
 * @author Danielly Cruz
 * @created July 27, 2003
 * @version 1.0
 */
public class MSDOSDirectoryException extends DirectoryException
{
  /**
   * Constructor for the MSDOSDirectoryException object
   *
   * @param menssageError Description of the Parameter
   */
  public MSDOSDirectoryException(String messageError)
  {
    super(messageError);
  }
}
