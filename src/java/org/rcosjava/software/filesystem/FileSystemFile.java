package org.rcosjava.software.filesystem;

/**
 * Description of the Interface
 *
 * @author Andrew Newman
 * @created July 27, 2003
 */
public interface FileSystemFile
{
  /**
   * Gets the firstBlock attribute of the FileSystemFile object
   *
   * @return The firstBlock value
   */
  public int getFirstBlock();

  /**
   * Gets the extension attribute of the FileSystemFile object
   *
   * @return The extension value
   */
  public String getExtension();

  /**
   * Gets the lastModificationDate attribute of the FileSystemFile object
   *
   * @return The lastModificationDate value
   */
  public String getLastModificationDate();

  /**
   * Gets the nameFile attribute of the FileSystemFile object
   *
   * @return The nameFile value
   */
  public String getNameFile();

  /**
   * Gets the numeberBlocksArchive attribute of the FileSystemFile object
   *
   * @return The numeberBlocksArchive value
   */
  public int getNumeberBlocksArchive();

  /**
   * Gets the sizeMSDOSFile attribute of the FileSystemFile object
   *
   * @return The sizeMSDOSFile value
   */
  public long getSizeMSDOSFile();

  /**
   * Sets the extension attribute of the FileSystemFile object
   *
   * @param extension The new extension value
   */
  public void setExtension(String extension);

  /**
   * Sets the firstBlock attribute of the FileSystemFile object
   *
   * @param firstBlock The new firstBlock value
   */
  public void setFirstBlock(int firstBlock);

  /**
   * Sets the lastModificationDate attribute of the FileSystemFile object
   *
   * @param lastModificationDate The new lastModificationDate value
   */
  public void setLastModificationDate(String lastModificationDate);

  /**
   * Sets the nameFile attribute of the FileSystemFile object
   *
   * @param nameFile The new nameFile value
   */
  public void setNameFile(String nameFile);

  /**
   * Sets the numeberBlocksArchive attribute of the FileSystemFile object
   *
   * @param numeberBlocksArchive The new numeberBlocksArchive value
   */
  public void setNumeberBlocksArchive(int numeberBlocksArchive);

  /**
   * Sets the sizeMSDOSFile attribute of the FileSystemFile object
   *
   * @param sizeMSDOSFile The new sizeMSDOSFile value
   */
  public void setSizeMSDOSFile(long sizeMSDOSFile);
}