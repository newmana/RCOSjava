//**************************************************************************
//FILE    : PriorityQueue.java
//PACKAGE : Util
//PURPOSE : Implements priority queue (based on string value).
//AUTHOR  : Andrew Newman
//MODIFIED:
//HISTORY : 02/02/96  First created.
//
//**************************************************************************

package Software.Util;

public class PriorityQueue extends Queue
{
  // Class constructor.
 
  public PriorityQueue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
  }

 // Priority Queue Insert.
  
 public void insert(Object theObject)
  {

    int position = 0;

    // If it's the first element in the queue then it will have the highest priority.

    if (queueEmpty() )
    {
      //System.out.println("First element in queue!! or less than or equal to first");
      insertElementAt(theObject, 0);
    }
    else
    {
      if ( compare( theObject, elementAt( position )) == 0 )
        insertElementAt(theObject, 0);
      else
      { 
        while ( compare(theObject, elementAt( position )) > 0 )
        {
          //System.out.println("Position: " + String.valueOf(position));
          position++;
          if ( position == size() )
            break;
         }
         insertElementAt(theObject, position);
         //System.out.println("Element insterted into " + String.valueOf(position));
       }
    }
  }
}
