package org.rcosjava.software.filesystem;

/**
 * Title: RCOS Description: Copyright: Copyright (c) 2002 Company: UFPE
 *
 * @author Danielly Cruz
 * @created July 27, 2003
 * @version 1.0
 */
public class AllocationTableException extends Exception
{
  /**
   * Constructor for the MSDOSFATException object
   *
   * @param menssageError Description of the Parameter
   */
  public AllocationTableException(String messageError)
  {
    super(messageError);
  }
}
