package org.rcosjava.software.filesystem.msdos;

import java.util.Vector;
import java.io.Serializable;

/**
 * Title:        RCOS
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      UFPE
 * @author Danielly Cruz
 * @version 1.0
 */

public class Disk implements Serializable{

  private Vector blocks;

  public Disk() {
    blocks = new Vector();
    for (int i = 0; i < 100; i++) {
       blocks.add(i, new Block());
    }
  }

  public Disk(int size) {
    blocks = new Vector(size);
    for (int i = 0; i < size; i++) {
       blocks.add(i, new Block());
    }
  }

  public void writeBlock(int address, Block block){
    try{
       blocks.add(address, block);
     }catch(Exception e){
       System.out.println("erro em writeBlock: "+e.getMessage());
       e.printStackTrace();
     }
  }

  public Block readBlock(int address){
     return (Block)blocks.elementAt(address);
  }

  public void removeBlock(int address){
     blocks.remove(address);
  }

}