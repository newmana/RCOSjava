package org.rcosjava.software.filesystem.msdos;

import org.rcosjava.software.filesystem.AllocationTableException;

/**
 * Title: RCOS Description: Copyright: Copyright (c) 2002 Company: UFPE
 *
 * @author Danielly Cruz
 * @created July 27, 2003
 * @version 1.0
 */
public class MSDOSFATException extends AllocationTableException
{
  /**
   * Constructor for the MSDOSFATException object
   *
   * @param menssageError Description of the Parameter
   */
  public MSDOSFATException(String messageError)
  {
    super(messageError);
  }
}
