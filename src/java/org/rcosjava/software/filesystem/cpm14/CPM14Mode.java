package org.rcosjava.software.filesystem.cpm14;

/**
 * Enumerated type of the different modes that a file can be in.
 * <P>
 * @author Andrew Newman
 * @created 28 July 2003
 */
public class CPM14Mode
{
  /**
   * File has no mode.
   */
  public final static int MODELESS = -1;

  /**
   * File has been allocated to a table entry.
   */
  public final static int ALLOCATED = 0;

  /**
   * File is being read.
   */
  public final static int READING = 1;

  /**
   * File is being written.
   */
  public final static int WRITING = 2;

  /**
   * File is being created.
   */
  public final static int CREATING = 3;

  /**
   * File is being deleted.
   */
  public final static int DELETING = 4;

  /**
   * File is being closed.
   */
  public final static int CLOSING = 5;
}