package org.rcosjava.software.filesystem.msdos;

import java.util.Hashtable;
import java.util.Enumeration;
import java.io.Serializable;
import org.rcosjava.software.filesystem.msdos.exception.MSDOSDirectoryException;

/**
 * Directory tem todo o controle da estrutura Hierárquica de diretório
 * do MS-DOS aqui simulado
 */
public class Directory implements Serializable{

   Hashtable msdosFiles;
   int quantityEntries;
   int quantityFree;
   int count;

   public Directory() {
      msdosFiles = new Hashtable();
      quantityEntries = 256;
      quantityFree = 256;
      count = 0;
   }

   public void addNewFile(MSDOSFile msdosFile) throws MSDOSDirectoryException{
      if(!msdosFiles.containsKey(msdosFile.getNameFile())){
         msdosFiles.put(msdosFile.getNameFile(), msdosFile);
      }else{
         throw new MSDOSDirectoryException(msdosFile.getNameFile()+ " already at Directory");
      }
      count++;
      quantityFree--;
   }

   public MSDOSFile readFile(String nameFile) throws MSDOSDirectoryException{
      MSDOSFile retorno = null;
      retorno = (MSDOSFile)msdosFiles.get(nameFile);
      if (retorno==null){
        throw new MSDOSDirectoryException("Diretory or File "+nameFile+" does not existe at root");
      }
      return retorno;
   }

   public void removeFile(String nameFile) throws MSDOSDirectoryException{
      MSDOSFile retorno = null;
      retorno = (MSDOSFile)msdosFiles.remove(nameFile);
      if (retorno==null){
        throw new MSDOSDirectoryException("Diretory or File "+nameFile+" does not existe at root");
      }
      count--;
      quantityFree++;
   }

    public Hashtable getMsdosFiles() {
       return msdosFiles;
    }

    public String dir(){
      String dir = "";
      Enumeration e = msdosFiles.elements();
      while(e.hasMoreElements()){
        MSDOSFile msdosFileAux = (MSDOSFile)e.nextElement();
        if(msdosFileAux.isA()){
           dir += msdosFileAux.getNameFile()+"."+msdosFileAux.getExtension()+"   "+msdosFileAux.getLastModificationDate()+"   "+msdosFileAux.getNumeberBlocksArchive()+"\n";
        }else{
           dir += msdosFileAux.getNameFile()+" <DIR>   "+msdosFileAux.getLastModificationDate()+"   "+msdosFileAux.getNumeberBlocksArchive()+"\n";
        }
      }
      return dir;
    }

    public int[] getFirstsEntriesFromCurrentDirectory(){
       int[] entries = new int[msdosFiles.size()];
       MSDOSFile msdosFile;
       Enumeration e = msdosFiles.elements();
       int i=0;
       while(e.hasMoreElements()) {
          msdosFile = (MSDOSFile)e.nextElement();
          entries[i] = msdosFile.getFirstBlock();
          i++;
       }
       return entries;
    }
}