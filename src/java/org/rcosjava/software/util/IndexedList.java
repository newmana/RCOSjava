package org.rcosjava.software.util;

/**
 * To maintain an indexable list of data. Indexing done for quick lookup time.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 8/5/2001 - Updated and made indexed list element an inner class. AN
 * </DD></DT>
 * <P>
 * @author Brett Carter.
 * @author Andrew Newman.
 * @created 9th March 1996
 * @version 1.00 $Date$
 */
public class IndexedList
{
  /**
   * Description of the Field
   */
  private int freeListIndex, freeListSize, dataListSize;

  /**
   * Description of the Field
   */
  private int[] freeList;

  /**
   * Description of the Field
   */
  private IndexedListElement[] dataList;

  /**
   * Description of the Field
   */
  private boolean full;

  /**
   * Default constructor. Creates a data size of 100 and a free list size of 10.
   */
  public IndexedList()
  {
    this(100, 10);
  }

  /**
   * Create a new data set with the given data size and list size.
   *
   * @param newDataSize the total number of data items
   * @param newFreeListSize Description of Parameter
   */
  public IndexedList(int newDataSize, int newFreeListSize)
  {
    // Setup class variables and sizes.
    dataListSize = newDataSize;
    freeListSize = newFreeListSize;
    full = false;

    // Make sure freelist is not larger than the Data List.
    if (freeListSize > dataListSize)
    {
      freeListSize = dataListSize;
    }

    // Allocate space for both lists.
    freeList = new int[freeListSize];
    dataList = new IndexedListElement[dataListSize];

    // Inititalize both lists.
    int counter;

    for (counter = 0; counter < dataListSize; counter++)
    {
      dataList[counter] = new IndexedListElement();
      dataList[counter].used = false;
    }
    for (counter = 0; counter < freeListSize; counter++)
    {
      freeList[counter] = counter;
    }
  }

  /**
   * Will determine if the given in position fits between the ranges of the
   * currently held data and will either return the value of the object held
   * there or return null if the index is out of range.
   *
   * @param existingItemPosition the index to find an existing item.
   * @return will return the object or null if it's not found.
   */
  public Object getItem(int existingItemPosition)
  {
    if ((existingItemPosition < dataListSize) &&
        (dataList[existingItemPosition].used))
    {
      return dataList[existingItemPosition].data;
    }
    else
    {
      return null;
    }
  }

  /**
   * Description of the Method
   *
   * @param newData Description of Parameter
   * @return Description of the Returned Value
   */
  public int add(Object newData)
  {
    if (!full)
    {
      int currentFreeIndex = freeList[freeListIndex];

      dataList[currentFreeIndex].data = newData;
      dataList[currentFreeIndex].used = true;
      freeListIndex++;
      if (freeListIndex == 10)
      {
        refillFreeList();
        full = true;
      }
      return currentFreeIndex;
    }
    else
    {
      return -1;
    }
  }

  /**
   * Description of the Method
   *
   * @param existingItem Description of Parameter
   * @return Description of the Returned Value
   */
  public int remove(int existingItem)
  {
    if ((existingItem < dataListSize) && (dataList[existingItem].used))
    {
      dataList[existingItem].used = false;
      if (freeListIndex > 0)
      {
        freeListIndex--;
        freeList[freeListIndex] = existingItem;
      }
      if (full)
      {
        full = false;
      }
      return existingItem;
    }
    else
    {
      return -1;
    }
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public int refillFreeList()
  {
    int counter = 0;

    while ((freeListIndex > 0) && (counter < dataListSize))
    {
      if (!dataList[counter].used)
      {
        freeListIndex--;
        freeList[freeListIndex] = counter;
      }
      counter++;
    }
    return 0;
  }

  /**
   * The simple object which contains an attribute "used" and the data.
   *
   * @author administrator
   * @created 28 April 2002
   */
  private static class IndexedListElement
  {
    /**
     * Description of the Field
     */
    public boolean used;
    /**
     * Description of the Field
     */
    public Object data;
  }
}
