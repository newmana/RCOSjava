//**************************************************************************
//FILE    : IndexedList
//PACKAGE : Util
//PURPOSE : To maintain an indexable list of data. Indexing done for
//          Quick lookup time. 
//AUTHOR  : Brett Carter
//MODIFIED: Andrew Newman
//HISTORY : 9/03/96  First created.
//
//**************************************************************************

package Software.Util;

class IndexedListElement
{
  public boolean cvUsed;
  public Object cvData;
}

public class IndexedList
{
  private int cvFreeListIndex, cvFreeListSize, cvDataListSize;
  private int[] cvFreeList;
  private IndexedListElement[] cvDataList;
  private boolean cvFull;

  public IndexedList(int mvDataSize, int mvFreeListSize)
  {
    init(mvDataSize, mvFreeListSize);
  }

  public IndexedList()
  {
    init(100, 10);
  }

  public void init(int mvDataSize, int mvFreeListSize)
  {
    // Setup class variables and sizes.
    cvDataListSize = mvDataSize;
    cvFreeListSize = mvFreeListSize;
    cvFull = false;
    
    // Make sure freelist is not larger than the Data List.
    if (cvFreeListSize > cvDataListSize)
    {
      cvFreeListSize = cvDataListSize;
    }

    // Allocate space for both lists.
    cvFreeList = new int[cvFreeListSize];
    cvDataList = new IndexedListElement[cvDataListSize];

    // Inititalize both lists.
    int mvCounter;
    for (mvCounter = 0; mvCounter < cvDataListSize; mvCounter++)
    {
      cvDataList[mvCounter] = new IndexedListElement();
      cvDataList[mvCounter].cvUsed = false;
    }
    for (mvCounter = 0; mvCounter < cvFreeListSize; mvCounter++)
    {
      cvFreeList[mvCounter] = mvCounter;
    }
    
  }

  public int add(Object mvData)
  {

    if (!cvFull)
    {
      int Current = cvFreeList[cvFreeListIndex];
      cvDataList[Current].cvData = mvData;
      cvDataList[Current].cvUsed = true;
      cvFreeListIndex++;
      if (cvFreeListIndex == 10)
      {
        refillFreeList();
      }
      if (cvFreeListIndex == 10)
      {
        cvFull = true;
      }
      return Current;
    }
    else
    {
      return -1;
    }
  }

  public int remove(int mvItem)
  {
    if ((mvItem < cvDataListSize) && (cvDataList[mvItem].cvUsed))
    {
      cvDataList[mvItem].cvUsed = false;
      if (cvFreeListIndex > 0)
      {
        cvFreeListIndex--;
        cvFreeList[cvFreeListIndex] = mvItem;
      }
      if (cvFull)
      {
        cvFull = false;
      }
      return mvItem;
    }
    else
    {
      return -1;
    }
  }  

  public int refillFreeList()
  {
    int mvCounter = 0;
    while ((cvFreeListIndex > 0) && (mvCounter < cvDataListSize))
    {
      if ( ! cvDataList[mvCounter].cvUsed)
      {
        cvFreeListIndex--;
        cvFreeList[cvFreeListIndex] = mvCounter;
      }
      mvCounter++;
    }
    return 0;
  }

  public Object getItem (int mvItem)
  {
    if ((mvItem < cvDataListSize) && (cvDataList[ mvItem ].cvUsed))
    {
      return cvDataList[mvItem].cvData;
    }
    else
    {
      return null;
    }  
  }
}
