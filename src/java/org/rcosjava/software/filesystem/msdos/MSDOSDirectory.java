package org.rcosjava.software.filesystem.msdos;

import java.util.Hashtable;
import java.util.Enumeration;
import java.io.Serializable;
import org.rcosjava.software.filesystem.msdos.MSDOSDirectoryException;

/**
 * Directory tem todo o controle da estrutura Hierárquica de diretório do MS-DOS
 * aqui simulado
 *
 * @author andrew
 * @created July 27, 2003
 */
public class MSDOSDirectory implements Serializable
{

  Hashtable msdosFiles;
  int quantityEntries;
  int quantityFree;
  int count;

  /**
   * Constructor for the MSDOSDirectory object
   */
  public MSDOSDirectory()
  {
    msdosFiles = new Hashtable();
    quantityEntries = 256;
    quantityFree = 256;
    count = 0;
  }

  /**
   * Adds a feature to the NewFile attribute of the MSDOSDirectory object
   *
   * @param msdosFile The feature to be added to the NewFile attribute
   * @throws MSDOSDirectoryException Description of the Exception
   */
  public void addNewFile(MSDOSFile msdosFile) throws MSDOSDirectoryException
  {
    if (!msdosFiles.containsKey(msdosFile.getNameFile()))
    {
      msdosFiles.put(msdosFile.getNameFile(), msdosFile);
    }
    else
    {
      throw new MSDOSDirectoryException(msdosFile.getNameFile() + " already at Directory");
    }
    count++;
    quantityFree--;
  }

  /**
   * Description of the Method
   *
   * @param nameFile Description of the Parameter
   * @return Description of the Return Value
   * @throws MSDOSDirectoryException Description of the Exception
   */
  public MSDOSFile readFile(String nameFile) throws MSDOSDirectoryException
  {
    MSDOSFile retorno = null;
    retorno = (MSDOSFile) msdosFiles.get(nameFile);
    if (retorno == null)
    {
      throw new MSDOSDirectoryException("Diretory or File " + nameFile + " does not existe at root");
    }
    return retorno;
  }

  /**
   * Description of the Method
   *
   * @param nameFile Description of the Parameter
   * @throws MSDOSDirectoryException Description of the Exception
   */
  public void removeFile(String nameFile) throws MSDOSDirectoryException
  {
    MSDOSFile retorno = null;
    retorno = (MSDOSFile) msdosFiles.remove(nameFile);
    if (retorno == null)
    {
      throw new MSDOSDirectoryException("Diretory or File " + nameFile + " does not existe at root");
    }
    count--;
    quantityFree++;
  }

  /**
   * Gets the msdosFiles attribute of the MSDOSDirectory object
   *
   * @return The msdosFiles value
   */
  public Hashtable getMsdosFiles()
  {
    return msdosFiles;
  }

  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public String dir()
  {
    String dir = "";
    Enumeration e = msdosFiles.elements();
    while (e.hasMoreElements())
    {
      MSDOSFile msdosFileAux = (MSDOSFile) e.nextElement();
      if (msdosFileAux.isA())
      {
        dir += msdosFileAux.getNameFile() + "." + msdosFileAux.getExtension() + "   " + msdosFileAux.getLastModificationDate() + "   " + msdosFileAux.getNumeberBlocksArchive() + "\n";
      }
      else
      {
        dir += msdosFileAux.getNameFile() + " <DIR>   " + msdosFileAux.getLastModificationDate() + "   " + msdosFileAux.getNumeberBlocksArchive() + "\n";
      }
    }
    return dir;
  }

  /**
   * Gets the firstsEntriesFromCurrentDirectory attribute of the MSDOSDirectory
   * object
   *
   * @return The firstsEntriesFromCurrentDirectory value
   */
  public int[] getFirstsEntriesFromCurrentDirectory()
  {
    int[] entries = new int[msdosFiles.size()];
    MSDOSFile msdosFile;
    Enumeration e = msdosFiles.elements();
    int i = 0;
    while (e.hasMoreElements())
    {
      msdosFile = (MSDOSFile) e.nextElement();
      entries[i] = msdosFile.getFirstBlock();
      i++;
    }
    return entries;
  }
}
