package org.rcosjava.software.filesystem.msdos;

import java.util.Hashtable;
import java.lang.Integer;
import org.rcosjava.software.filesystem.msdos.exception.MSDOSFATException;
import java.io.Serializable;
import java.util.Vector;
import java.util.StringTokenizer;

public class MSDOSFAT implements Serializable{

  private int sizeFAT;
  private int sizeFreeFAT;
  private Hashtable entriesFAT;

  public MSDOSFAT(int newSizeFAT){

   this.sizeFAT = newSizeFAT;
   this.sizeFreeFAT = newSizeFAT;
   entriesFAT = new Hashtable();
   for (int i = 1; i <= sizeFAT; i++) {
      entriesFAT.put(new Integer(i), new Integer(-1));
   }
  }

  public MSDOSFAT(){

   this.sizeFAT = 100;
   this.sizeFreeFAT = 100;
   entriesFAT = new Hashtable();
   for (int i = 1; i <= sizeFAT; i++) {
      entriesFAT.put(new Integer(i), new Integer(-1));
   }
  }

  public Hashtable getEntriesFAT() {
     return entriesFAT;
  }

  public void addEntriesFAT(int index, int value) throws MSDOSFATException{
   Integer idIndex = new Integer(index);
   Integer idValue = new Integer(value);

   // Veja se jah existe algo com esse valor
   System.out.println("index: "+index);
   System.out.println("value: "+value);
   Integer indexAux = (Integer)entriesFAT.get(new Integer(index));
   System.out.println("indexAux.intValue(): "+indexAux.intValue());
   if(indexAux.intValue() == -3){
      entriesFAT.put(idIndex, idValue);
      this.sizeFreeFAT = this.sizeFreeFAT-1;
   }else{
      throw new MSDOSFATException("Value already at FAT ");
   }

  }

  public int getEntryFAT(int index) throws MSDOSFATException{

     Integer indexAux = (Integer)entriesFAT.get(new Integer(index));
     int aux = -1;
     System.out.println("indexAux.intValue(): "+indexAux.intValue());
     if (indexAux.intValue()==-1){
        throw new MSDOSFATException("There is not value for this key at FAT");
     }else{
        aux = indexAux.intValue();
     }
     return aux;
  }

  public int[] getEntriesFileFromFAT(int index) throws MSDOSFATException{

     Integer valueAux = (Integer)entriesFAT.get(new Integer(index));
     String aux = "";

     System.out.println("valueAux.intValue(): "+valueAux.intValue());

     if (valueAux.intValue()==-1){
        throw new MSDOSFATException("There is not value for this key at FAT");
     }else{
        aux = index+",";
        while(valueAux.intValue()!=-2){
           aux += valueAux.intValue()+",";
           valueAux = (Integer)entriesFAT.get(new Integer(valueAux.intValue()));
        }
        aux = aux.substring(0, aux.length()-1);
     }

     System.out.println("auxEntries: "+aux);
     StringTokenizer st = new StringTokenizer(aux, ",");
     System.out.println("st.countTokens(): "+st.countTokens());
     int[] indexes = new int[st.countTokens()];
     int tamanho = st.countTokens();
//     for (int i = 0; i <= st.countTokens(); i++) {
     int i=0;
     while(i<tamanho){
       System.out.println("i "+i);
       indexes[i] = Integer.parseInt(st.nextToken());
       System.out.println("indexes["+i+"] "+indexes[i]);
       i=i+1;
     }

     System.out.println("retornando "+indexes.length);
     return indexes;
  }

  public int getNewEntryFAT() throws MSDOSFATException{
     boolean flag = entriesFAT.containsValue(new Integer(-1));
     Integer value;
     int aux=0;
     boolean flag2 = false;
     int i = 1;

     if (flag){
       while(i <= this.sizeFAT && !flag2) {
          value = (Integer)entriesFAT.get(new Integer(i));
          if (value.intValue()==-1){
            flag2 = true;
          }
          i++;
       }
    }else{
       throw new MSDOSFATException("FAT is full");
    }
    // setar como ocupado temporariamente senao qdo
    // pedir nova entrada ele me devolve a mesma entrada,
    // ou seja, o mesmo carinha
    entriesFAT.put(new Integer(i-1), new Integer(-3));
     return (i-1);
  }

  public void removeEntryFAT(int index) throws MSDOSFATException{
     Integer indexAux = (Integer)entriesFAT.get(new Integer(index));
     if (indexAux.intValue()!=-1){
        entriesFAT.put(new Integer(index), new Integer(-1));
        this.sizeFreeFAT = this.sizeFreeFAT+1;
     }else{
        throw new MSDOSFATException("There is not value for this key at FAT");
     }
  }

  public void removeEntriesOfFAT(int[] indexes) throws MSDOSFATException{
     for (int i = 0; i < indexes.length; i++) {
        removeEntryFAT(indexes[i]);
     }
  }

  public int getSizeFreeFAT() {
    return sizeFreeFAT;
  }
  public int getSizeFAT() {
    return sizeFAT;
  }
  public void setSizeFAT(int sizeFAT) {
    this.sizeFAT = sizeFAT;
  }
  public void setSizeFreeFAT(int sizeFreeFAT) {
    this.sizeFreeFAT = sizeFreeFAT;
  }

}