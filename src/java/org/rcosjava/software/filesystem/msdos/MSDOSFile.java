package org.rcosjava.software.filesystem.msdos;

import java.util.Date;
import org.rcosjava.software.util.Horario;
import java.io.Serializable;

public class MSDOSFile implements Serializable{

   String nameFile;
   String extension;
   boolean A,D,V,S,H;
//   Date lastModificationDate;
   String lastModificationDate;
   Horario lastModificationHour;
   int firstBlock;
   long sizeMSDOSFile;
   int numeberBlocksArchive;

   public MSDOSFile(String newNameFile, String newExtension, boolean newA, boolean newD,
                    boolean newV, boolean newS, boolean newH, String newLastModificationDate,
                    Horario newLastModificationHour, int newFirstBlock, long newSizeMSDOSFile,
                    int newNumeberBlocksArchive) {
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

   public boolean isA() {
    return A;
   }
   public boolean isD() {
    return D;
   }
   public int getFirstBlock() {
    return firstBlock;
   }
   public String getExtension() {
    return extension;
   }
   public boolean isH() {
    return H;
   }
   public String getLastModificationDate() {
    return lastModificationDate;
   }
   public Horario getLastModificationHour() {
    return lastModificationHour;
   }
   public String getNameFile() {
    return nameFile;
   }
   public int getNumeberBlocksArchive() {
    return numeberBlocksArchive;
   }
   public boolean isS() {
    return S;
   }
   public long getSizeMSDOSFile() {
    return sizeMSDOSFile;
   }
   public void setA(boolean A) {
    this.A = A;
   }
   public void setD(boolean D) {
    this.D = D;
   }
   public void setExtension(String extension) {
    this.extension = extension;
   }
   public void setFirstBlock(int firstBlock) {
    this.firstBlock = firstBlock;
   }
   public void setH(boolean H) {
    this.H = H;
   }
   public void setLastModificationDate(String lastModificationDate) {
    this.lastModificationDate = lastModificationDate;
   }
   public void setLastModificationHour(Horario lastModificationHour) {
    this.lastModificationHour = lastModificationHour;
   }
   public void setNameFile(String nameFile) {
    this.nameFile = nameFile;
   }
   public void setNumeberBlocksArchive(int numeberBlocksArchive) {
    this.numeberBlocksArchive = numeberBlocksArchive;
   }
   public void setS(boolean S) {
    this.S = S;
   }
   public void setSizeMSDOSFile(long sizeMSDOSFile) {
    this.sizeMSDOSFile = sizeMSDOSFile;
   }
   public void setV(boolean V) {
    this.V = V;
   }
}