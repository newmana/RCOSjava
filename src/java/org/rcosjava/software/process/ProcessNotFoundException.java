package org.rcosjava.software.process;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Could not find an process from a queue.
 * <P>
 * @author Andrew Newman.
 * @created 15th November 2002
 * @version 1.00 $Date$
 */
public class ProcessNotFoundException extends Exception
{
  /**
   * Create a new exception with the given reason.
   *
   * @param message the reason or message given for the exception being thrown.
   */
  public ProcessNotFoundException(String message)
  {
    super(message);
  }
}
