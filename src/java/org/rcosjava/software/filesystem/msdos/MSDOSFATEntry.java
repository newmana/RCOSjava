package org.rcosjava.software.filesystem.msdos;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class MSDOSFATEntry {

 int idFAT;
 int idAdressBlock;

 public MSDOSFATEntry() {
    this.idFAT = -1;
    this.idAdressBlock = -1;
 }

 public MSDOSFATEntry(int newIdFAT, int newIdAdressBlock) {
    this.idFAT = newIdFAT;
    this.idAdressBlock = newIdAdressBlock;
 }

 public int getIdFAT() {
  return idFAT;
 }

 public void setIdFAT(int idFAT) {
  this.idFAT = idFAT;
 }

 public int getIdAdressBlock() {
  return idAdressBlock;
 }

 public void setIdAdressBlock(int idAdressBlock) {
  this.idAdressBlock = idAdressBlock;
 }

}