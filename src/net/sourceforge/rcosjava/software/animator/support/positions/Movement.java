//**************************************************************************/
// FILE     : Movement.java
// PACKAGE  : Animator.Positions
// PURPOSE  : This class is designed to manipulate the Position and
//            Position chain objects.  It allows a chain of positions
//            to be created and for these positions to move from the
//            beginning of the chain to the end.  By doing this you
//            can create screen objects that can be plotted and moved
//            based on predetermined co-ordinates.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 17/12/96  First created.
//
//**************************************************************************/

package net.sourceforge.rcosjava.software.animator.support.positions;

import net.sourceforge.rcosjava.software.util.LIFOQueue;

public class Movement

// PositionChain is a vector class which holds Position objects.
// pCurrentPosition and pNextPosition contain the current and
// next Position objects in the PositionChain.  iCurrentX and
// iCurrentY contain the current X,Y co-ordinates of where the
// object is.  iCurrentPosition is the index to the
// PositionChain.  If bForward is true then the object moves from
// the current X,Y position to the next X,Y position.

{
  private LIFOQueue positions;
  private Position pCurrentPosition, pNextPosition;
  private boolean bFinished;
  public int iCurrentX, iCurrentY, iCurrentPosition;
  public boolean bForward;
  public boolean bRepeat;

//Default initialisation.

  public Movement()
  {
    super();
    positions = new LIFOQueue(10,10);
    pCurrentPosition = new Position(0,0,0,0);
    pNextPosition = new Position(0,0,0,0);
    iCurrentX = 0;
    iCurrentY = 0;
    bForward = true;
    bRepeat = false;
    bFinished = true;
  }

//Able to set whether the item repeats or not.

  public Movement(boolean bInitRepeat)
  {
    super();
    positions = new LIFOQueue(10,10);
    pCurrentPosition = new Position(0,0,0,0);
    pNextPosition = new Position(0,0,0,0);
    iCurrentX = 0;
    iCurrentY = 0;
    bForward = true;
    bRepeat = bInitRepeat;
    bFinished = true;
  }

  // Current position in queue of Positions

  public int currentPosition()
  {
    return positions.thePointer;
  }

// Adds a position to the PostionChain.

  public void addPosition(Position newPosition)
  {
    positions.insert(newPosition);
  }

// Sets the variables to be set to the start of the PositionChain.

  public synchronized void start()
  {
    if (positions.itemCount() > 1)
    {
      bFinished = false;
      iCurrentPosition = 0;
      positions.goToHead();
      pCurrentPosition = (Position) positions.peek();
      iCurrentX = pCurrentPosition.getX();
      iCurrentY = pCurrentPosition.getY();
      positions.goToNext();
      pNextPosition = (Position) positions.peek();
    }
  }

  public synchronized boolean finished()
  {
    return (bFinished);
  }

  public synchronized boolean finished(int iTo)
  {
    return (positions.thePointer == iTo);
  }

// Steps forward or backward one motion depending on whether bForward
// is true or not.

  public synchronized void step ()
  {
    if (bForward)
    {
      this.forward();
    }
    else
    {
      this.back();
    }
  }

  private void forward()
  {
    if ((positions.itemCount() > 1) && (!bFinished))
    {
      iCurrentX += pCurrentPosition.getDirectionX();
      iCurrentY += pCurrentPosition.getDirectionY();

//      System.out.println("Current X,Y= " + iCurrentX + " " + iCurrentY);
//      System.out.println("Item Count " + iCurrentPosition + " Max " + positions.itemCount());
//      System.out.println("next x,y = " + pNextPosition.iDirectionX + " " + pNextPosition.iDirectionY);

      if (((pCurrentPosition.getDirectionX() > 0) &&
           (iCurrentX >= pNextPosition.getX())) ||
          ((pCurrentPosition.getDirectionX() < 0) &&
           (iCurrentX <= pNextPosition.getX())) ||
          ((pCurrentPosition.getDirectionY() > 0) &&
           (iCurrentY >= pNextPosition.getY())) ||
          ((pCurrentPosition.getDirectionY() < 0) &&
           (iCurrentY <= pNextPosition.getY())))
      {
        if ((pNextPosition.getDirectionX() != 0) ||
            (pNextPosition.getDirectionY() != 0))
        {
          pCurrentPosition = pNextPosition;
          positions.goToNext();
          pNextPosition = (Position) positions.peek();
          iCurrentPosition++;
        }
        else
        {
          if (!bRepeat)
          {
            iCurrentX = pNextPosition.getX();
            iCurrentY = pNextPosition.getY();
            bFinished = true;
          }
          else
          {
            this.start();
          }
        }
      }
    }
  }

  private void back()
  {
    // Something in here to be added later.
  }
}
