package net.sourceforge.rcosjava.software.animator.support.positions;

/**
 * This class is designed to hold an X,Y value representing the start point
 * of a set of movement.  The Delta X and Delta Y represent the steps to take
 * moving away from the given X,Y value.
 * <P>
 * @author Andrew Newman
 * @version 1.00 $Date$
 * @created 17th December 1996
 */
public class Position
{
  private int xPosition;
  private int yPosition;
  private int deltaX;
  private int deltaY;

  /**
   * Creates a new position object with the given value and sets the default
   * delta X and delta Y to 1.
   *
   * @param newXPosition the X co-ordinate to set the position to.
   * @param newYPosition the Y co-ordinate to set the position to.
   */
  public Position (int newXPosition, int newYPosition)
  {
    xPosition = newXPosition;
    yPosition = newYPosition;
    deltaX = 1;
    deltaY = 1;
  }

  /**
   * Creates a new position object with the given X,Y co-ordinates and delta
   * settings.
   *
   * @param newXPosition the X co-ordinate to set the position to.
   * @param newYPosition the Y co-ordinate to set the position to.
   * @param newDeltaX the change in the X position.
   * @param newDeltaY the change in the Y position.
   */
  public Position (int newXPosition, int newYPosition, int newDeltaX,
    int newDeltaY)
  {
    xPosition = newXPosition;
    yPosition = newYPosition;
    deltaX = newDeltaX;
    deltaY = newDeltaY;
  }

  /**
   * @return the current X starting co-ordinate of the position.
   */
  public int getX()
  {
    return xPosition;
  }

  /**
   * @return the current Y starting co-ordinate of the position.
   */
  public int getY()
  {
    return yPosition;
  }

  /**
   * @return the current X change in starting point.
   */
  public int getDeltaX()
  {
    return deltaX;
  }

  /**
   * @return the current Y change in starting point.
   */
  public int getDeltaY()
  {
    return deltaY;
  }
}
