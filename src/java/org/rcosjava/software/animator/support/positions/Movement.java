package org.rcosjava.software.animator.support.positions;
import org.rcosjava.software.util.LIFOQueue;

/**
 * This class is designed to manipulate the Position and Position chain objects.
 * It allows a chain of positions to be created and for these positions to move
 * from the beginning of the chain to the end. By doing this you can create
 * screen objects that can be plotted and moved based on predetermined
 * co-ordinates.
 * <P>
 * @author Andrew Newman
 * @created 17th December 1996
 * @version 1.00 $Date$
 */
public class Movement
{
  /**
   * Contains all of the position objects
   */
  private LIFOQueue positions;

  /**
   * Current position in chain.
   */
  private Position currentPosition;

  /**
   * Next position in chain.
   */
  private Position nextPosition;

  /**
   * True if the movement has completed (and not looping).
   */
  private boolean finishedMoving;

  /**
   * Current X position of the object.
   */
  private int currentX;

  /**
   * Current Y position of the object.
   */
  private int currentY;

  /**
   * Current index to the position in the chain of positions.
   */
  private int currentIndexPosition;

  /**
   * Whether to move forward in positions (or backwards if false).
   */
  private boolean moveForward;

  /**
   * Whether to loop the positions once the end has been reached.
   */
  private boolean repeatMovement;

  /**
   * Default constructor.
   */
  public Movement()
  {
    this(false);
  }

  /**
   * Able to set whether the item repeats or not.
   *
   * @param newRepeatMovement Description of Parameter
   */
  public Movement(boolean newRepeatMovement)
  {
    super();
    positions = new LIFOQueue(10, 10);
    currentPosition = new Position(0, 0, 0, 0);
    nextPosition = new Position(0, 0, 0, 0);
    currentX = 0;
    currentY = 0;
    moveForward = true;
    repeatMovement = newRepeatMovement;
    finishedMoving = true;
  }

  /**
   * @return the current x value
   */
  public synchronized int getCurrentX()
  {
    return currentX;
  }

  /**
   * @return the current y value
   */
  public synchronized int getCurrentY()
  {
    return currentY;
  }

  /**
   * @return the current position in queue of Positions
   */
  public synchronized int currentPosition()
  {
    return positions.getPointer();
  }

  /**
   * Adds a position to the Postion Chain.
   *
   * @param newPosition The feature to be added to the Position attribute
   */
  public synchronized void addPosition(Position newPosition)
  {
    positions.insert(newPosition);
  }

  /**
   * Sets the variables to be set to the start of the PositionChain.
   */
  public synchronized void start()
  {
    if (positions.itemCount() > 1)
    {
      finishedMoving = false;
      currentIndexPosition = 0;
      positions.goToHead();
      currentPosition = (Position) positions.peek();
      currentX = currentPosition.getX();
      currentY = currentPosition.getY();
      positions.goToNext();
      nextPosition = (Position) positions.peek();
    }
  }

  /**
   * @return whether the movement has been completed to the end or not.
   */
  public synchronized boolean finished()
  {
    return (finishedMoving);
  }

  /**
   * This will return whether or not a certain position in the chain has been
   * reached (exactly).
   *
   * @param toPosition the position to check for.
   * @return whether the current pointer matched the given position.
   */
  public synchronized boolean finished(int toPosition)
  {
    return (positions.getPointer() == toPosition);
  }

  /**
   * Steps forward or backward one motion depending on whether moveForward is
   * true or not.
   */
  public synchronized void step()
  {
    if (moveForward)
    {
      this.forward();
    }
    else
    {
      this.back();
    }
  }

  /**
   * Move to the next position.
   */
  private synchronized void forward()
  {
    if ((positions.itemCount() > 1) && (!finishedMoving))
    {
      currentX += currentPosition.getDeltaX();
      currentY += currentPosition.getDeltaY();

      if (((currentPosition.getDeltaX() > 0) &&
          (currentX >= nextPosition.getX())) ||
          ((currentPosition.getDeltaX() < 0) &&
          (currentX <= nextPosition.getX())) ||
          ((currentPosition.getDeltaY() > 0) &&
          (currentY >= nextPosition.getY())) ||
          ((currentPosition.getDeltaY() < 0) &&
          (currentY <= nextPosition.getY())))
      {
        if ((nextPosition.getDeltaX() != 0) ||
            (nextPosition.getDeltaY() != 0))
        {
          currentPosition = nextPosition;
          positions.goToNext();
          nextPosition = (Position) positions.peek();
          currentIndexPosition++;
        }
        else
        {
          if (!repeatMovement)
          {
            currentX = nextPosition.getX();
            currentY = nextPosition.getY();
            finishedMoving = true;
          }
          else
          {
            this.start();
          }
        }
      }
    }
  }

  /**
   * Not implemented. But will go backwards through the positions if required.
   */
  private void back()
  {
    // Something in here to be added later.
  }
}
