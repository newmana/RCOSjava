package org.rcosjava.software.filesystem.msdos;

import java.util.Date;
import org.rcosjava.software.util.Horario;
import java.io.Serializable;
import org.rcosjava.software.filesystem.FileSystemFile;

/**
 * Description of the Class
 *
 * @author andrew
 * @created July 27, 2003
 */
public class MSDOSFile implements Serializable, FileSystemFile
{
  String nameFile;
  String extension;
  boolean A, D, V, S, H;
//   Date lastModificationDate;
  String lastModificationDate;
  Horario lastModificationHour;
  int firstBlock;
  long sizeMSDOSFile;
  int numeberBlocksArchive;


  /**
   * Constructor for the MSDOSFile object
   *
   * @param newNameFile Description of the Parameter
   * @param newExtension Description of the Parameter
   * @param newA Description of the Parameter
   * @param newD Description of the Parameter
   * @param newV Description of the Parameter
   * @param newS Description of the Parameter
   * @param newH Description of the Parameter
   * @param newLastModificationDate Description of the Parameter
   * @param newLastModificationHour Description of the Parameter
   * @param newFirstBlock Description of the Parameter
   * @param newSizeMSDOSFile Description of the Parameter
   * @param newNumeberBlocksArchive Description of the Parameter
   */
  public MSDOSFile(String newNameFile, String newExtension, boolean newA,
      boolean newD, boolean newV, boolean newS, boolean newH,
      String newLastModificationDate, Horario newLastModificationHour,
      int newFirstBlock, long newSizeMSDOSFile, int newNumeberBlocksArchive)
  {
    this.nameFile = newNameFile;
    this.extension = newExtension;
    this.A = newA;
    this.D = newD;
    this.V = newV;
    this.S = newS;
    this.H = newH;
    this.lastModificationDate = newLastModificationDate;
    this.lastModificationHour = newLastModificationHour;
    this.firstBlock = newFirstBlock;
    this.sizeMSDOSFile = newSizeMSDOSFile;
    this.numeberBlocksArchive = newNumeberBlocksArchive;
  }


  /**
   * Gets the a attribute of the MSDOSFile object
   *
   * @return The a value
   */
  public boolean isA()
  {
    return A;
  }


  /**
   * Gets the d attribute of the MSDOSFile object
   *
   * @return The d value
   */
  public boolean isD()
  {
    return D;
  }


  /**
   * Gets the firstBlock attribute of the MSDOSFile object
   *
   * @return The firstBlock value
   */
  public int getFirstBlock()
  {
    return firstBlock;
  }


  /**
   * Gets the extension attribute of the MSDOSFile object
   *
   * @return The extension value
   */
  public String getExtension()
  {
    return extension;
  }


  /**
   * Gets the h attribute of the MSDOSFile object
   *
   * @return The h value
   */
  public boolean isH()
  {
    return H;
  }


  /**
   * Gets the lastModificationDate attribute of the MSDOSFile object
   *
   * @return The lastModificationDate value
   */
  public String getLastModificationDate()
  {
    return lastModificationDate;
  }


  /**
   * Gets the lastModificationHour attribute of the MSDOSFile object
   *
   * @return The lastModificationHour value
   */
  public Horario getLastModificationHour()
  {
    return lastModificationHour;
  }


  /**
   * Gets the nameFile attribute of the MSDOSFile object
   *
   * @return The nameFile value
   */
  public String getNameFile()
  {
    return nameFile;
  }


  /**
   * Gets the numeberBlocksArchive attribute of the MSDOSFile object
   *
   * @return The numeberBlocksArchive value
   */
  public int getNumeberBlocksArchive()
  {
    return numeberBlocksArchive;
  }


  /**
   * Gets the s attribute of the MSDOSFile object
   *
   * @return The s value
   */
  public boolean isS()
  {
    return S;
  }


  /**
   * Gets the sizeMSDOSFile attribute of the MSDOSFile object
   *
   * @return The sizeMSDOSFile value
   */
  public long getSizeMSDOSFile()
  {
    return sizeMSDOSFile;
  }


  /**
   * Sets the a attribute of the MSDOSFile object
   *
   * @param A The new a value
   */
  public void setA(boolean A)
  {
    this.A = A;
  }


  /**
   * Sets the d attribute of the MSDOSFile object
   *
   * @param D The new d value
   */
  public void setD(boolean D)
  {
    this.D = D;
  }


  /**
   * Sets the extension attribute of the MSDOSFile object
   *
   * @param extension The new extension value
   */
  public void setExtension(String extension)
  {
    this.extension = extension;
  }


  /**
   * Sets the firstBlock attribute of the MSDOSFile object
   *
   * @param firstBlock The new firstBlock value
   */
  public void setFirstBlock(int firstBlock)
  {
    this.firstBlock = firstBlock;
  }


  /**
   * Sets the h attribute of the MSDOSFile object
   *
   * @param H The new h value
   */
  public void setH(boolean H)
  {
    this.H = H;
  }


  /**
   * Sets the lastModificationDate attribute of the MSDOSFile object
   *
   * @param lastModificationDate The new lastModificationDate value
   */
  public void setLastModificationDate(String lastModificationDate)
  {
    this.lastModificationDate = lastModificationDate;
  }


  /**
   * Sets the lastModificationHour attribute of the MSDOSFile object
   *
   * @param lastModificationHour The new lastModificationHour value
   */
  public void setLastModificationHour(Horario lastModificationHour)
  {
    this.lastModificationHour = lastModificationHour;
  }


  /**
   * Sets the nameFile attribute of the MSDOSFile object
   *
   * @param nameFile The new nameFile value
   */
  public void setNameFile(String nameFile)
  {
    this.nameFile = nameFile;
  }


  /**
   * Sets the numeberBlocksArchive attribute of the MSDOSFile object
   *
   * @param numeberBlocksArchive The new numeberBlocksArchive value
   */
  public void setNumeberBlocksArchive(int numeberBlocksArchive)
  {
    this.numeberBlocksArchive = numeberBlocksArchive;
  }


  /**
   * Sets the s attribute of the MSDOSFile object
   *
   * @param S The new s value
   */
  public void setS(boolean S)
  {
    this.S = S;
  }


  /**
   * Sets the sizeMSDOSFile attribute of the MSDOSFile object
   *
   * @param sizeMSDOSFile The new sizeMSDOSFile value
   */
  public void setSizeMSDOSFile(long sizeMSDOSFile)
  {
    this.sizeMSDOSFile = sizeMSDOSFile;
  }


  /**
   * Sets the v attribute of the MSDOSFile object
   *
   * @param V The new v value
   */
  public void setV(boolean V)
  {
    this.V = V;
  }
}
