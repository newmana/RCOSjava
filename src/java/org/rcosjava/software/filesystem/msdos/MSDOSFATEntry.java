package org.rcosjava.software.filesystem.msdos;

/**
 * Title: RCOS Description: Copyright: Copyright (c) 2002 Company: UFPE
 *
 * @author Danielly Cruz
 * @created July 27, 2003
 * @version 1.0
 */
public class MSDOSFATEntry
{
  int idFAT;
  int idAdressBlock;

  /**
   * Constructor for the MSDOSFATEntry object
   */
  public MSDOSFATEntry()
  {
    this.idFAT = -1;
    this.idAdressBlock = -1;
  }

  /**
   * Constructor for the MSDOSFATEntry object
   *
   * @param newIdFAT Description of the Parameter
   * @param newIdAdressBlock Description of the Parameter
   */
  public MSDOSFATEntry(int newIdFAT, int newIdAdressBlock)
  {
    this.idFAT = newIdFAT;
    this.idAdressBlock = newIdAdressBlock;
  }

  /**
   * Gets the idFAT attribute of the MSDOSFATEntry object
   *
   * @return The idFAT value
   */
  public int getIdFAT()
  {
    return idFAT;
  }

  /**
   * Sets the idFAT attribute of the MSDOSFATEntry object
   *
   * @param idFAT The new idFAT value
   */
  public void setIdFAT(int idFAT)
  {
    this.idFAT = idFAT;
  }

  /**
   * Gets the idAdressBlock attribute of the MSDOSFATEntry object
   *
   * @return The idAdressBlock value
   */
  public int getIdAdressBlock()
  {
    return idAdressBlock;
  }

  /**
   * Sets the idAdressBlock attribute of the MSDOSFATEntry object
   *
   * @param idAdressBlock The new idAdressBlock value
   */
  public void setIdAdressBlock(int idAdressBlock)
  {
    this.idAdressBlock = idAdressBlock;
  }
}
