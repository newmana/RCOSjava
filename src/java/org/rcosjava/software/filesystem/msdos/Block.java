package org.rcosjava.software.filesystem.msdos;

import java.io.Serializable;
import org.rcosjava.software.filesystem.msdos.util.InterfaceMSDOS;

public class Block implements Serializable{

 byte[] data;

 public Block() {
    // tamanho de 4k = 4096 bytes
    data = new byte[InterfaceMSDOS.BLOCK_SIZE];
 }

 public byte[] getData() {
  return data;
 }

 public void setData(byte[] newData) {
  this.data = newData;
 }

 public Object clone(){
    Block blockAux = new Block();
    blockAux.setData(this.data);
    return blockAux;
 }

}