package org.rcosjava.messaging.postoffices.universal;

import java.io.Serializable;
import java.util.Comparator;

/**
 * There are no more messages left to read.
 * <P>
 * @author Andrew Newman.
 * @created 15th November 2002
 * @version 1.00 $Date$
 */
public class EndOfMessagesException extends Exception
{
  /**
   * Create a new exception with the given reason.
   *
   * @param message the reason or message given for the exception being thrown.
   */
  public EndOfMessagesException(String message)
  {
    super(message);
  }
}
