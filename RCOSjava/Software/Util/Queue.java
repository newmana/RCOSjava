//**************************************************************************
//FILE    : Queue.java
//PACKAGE : Util
//PURPOSE : Implements base Queue type (FIFO).
//AUTHOR  : Andrew Newman
//MODIFIED:
//HISTORY : 02/02/96  First created.
//
//**************************************************************************

package Software.Util;

import java.util.*;

public class Queue extends Vector
{
  // This is an integer that points to an element in the queue.

  public int thePointer;

  // Default constructor.

  public Queue()
  {
   super();
   thePointer = 0;
  }

  // Constructor of the queue.
  public Queue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
    thePointer = 0;
  }

  // Default Queue Insert - FIFO Type.
  public void insert(Object theObject)
  {
    insertElementAt(theObject, 0);
  }
  
  // Remove an element at the top of the queue and return it.
  public Object retrieve()
  {
    if (queueEmpty()) return null;

    Object theObject = elementAt(itemCount()-1);
    
    removeElementAt(itemCount()-1);
    return theObject;
  }

  //**********************************************************/
  // remove the element currently pointed to by thePointer
  public Object retrieveCurrent()
  {
    if (queueEmpty()) return null;

    Object theObject = elementAt(thePointer);
    
    removeElementAt(thePointer);
    return theObject;
  }

  // Return -1, 0, 1 if the first object is less than, equal to or
  // greater than the second object.

  public int compare (Object first, Object second)
  {
    String firstString = first.toString();
    String secondString = second.toString();
    return (firstString.compareTo(secondString));
  }
  
  // Return position of an object.
  // Return position or -1 if not found.
  
  public int position (Object item)
  {
    int iWhere, iCurrentPos;
    iWhere = 0;
    iCurrentPos = thePointer;
    goToHead();
    while (!atTail())
    {
      if (compare(item,retrieveCurrent()) == 0)
      {
        iWhere = thePointer;
        thePointer = iCurrentPos;
        return iWhere;
      }
      goToNext();
    }
    return -1;
  }

  // Return an element of the queue.

  public Object peek(int i)
  {
    if (queueEmpty()) return null;
    return elementAt(i);
  }

  //*************************************
  // Return element at thePointer location

  public Object peek()
  {
    if (queueEmpty()) return null;
    return elementAt(thePointer);
  }

  // Returns True if the queue is empty.

  public boolean queueEmpty()
  {
    return (this.size() == 0);
  }

  // Returns the size of the queue.
 
  public int itemCount()
  {
    return (this.size());
  }

  // Set the pointer to anywhere in the Queue

  public void goTo (int i)
  {
    if ((i <= this.size()-1) && (i >= 0))
    {
      thePointer = i;
    }
  }

  // Set the pointer to the head of the Queue

  public void goToHead()
  {
    thePointer = 0;
  }

  //*****************************
  // move to next element

  public void goToNext()
  {
    if (!atTail())
      thePointer++;
  }

  //****************************
  // move to previous element

  public void goToPrevious()
  {
    if (thePointer != 0)
      thePointer--;
  }

  // Sets the pointer to the last element in the queue.

  public void goToTail()
  {
    thePointer = this.size()-1;
  }

  //*****************************
  // return TRUE if at head of list
 
  public boolean atHead()
  {
    return (thePointer == 0);
  }

  //*****************************
  // return TRUE if at end of list
 
  public boolean atTail()
  {
    return (thePointer == this.size());
  }
}
