package org.rcosjava.software.filesystem.cpm14;

/**
 * Enumerated type of the different modes that a file can be in.
 * <P>
 * @author Andrew Newman
 * @created 28 July 2003
 */
public class CPM14TableOffset
{
  /**
   * The offset in the entry table as to where to put the status byte.
   */
  public final static int STATUS = 0;

  /**
   * The offset to start writing the filename.
   */
  public final static int FILENAME = 1;

  /**
   * The offset to start writing the file extension.
   */
  public final static int EXTENSION = 9;

  /**
   * The offset to start writing the extension entry.
   */
  public final static int EXTENT = 12;

  /**
   * Reserved field.
   */
  public final static int RESERVED = 13;

  /**
   * Stores the number of records an entry has.
   */
  public final static int RECORDS = 15;

  /**
   * The offset into the file system of where the data blocks are stored.
   */
  public final static int DATA_BLOCKS = 16;
}