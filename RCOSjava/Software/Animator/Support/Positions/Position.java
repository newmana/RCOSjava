//**************************************************************************/
// FILE     : Positions.java
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

package Software.Animator.Support.Positions;

public class Position
{
  private int iX;
  private int iY;
  private int iDirectionX;
  private int iDirectionY;

  public Position (int iX, int iY)
  {
    super();
    this.iX = iX;
    this.iY = iY;
    this.iDirectionX = 1;
    this.iDirectionY = 1;
  }

  public Position (int iX, int iY, int iDirectionX, int iDirectionY)
  {
    super();
    this.iX = iX;
    this.iY = iY;
    this.iDirectionX = iDirectionX;
    this.iDirectionY = iDirectionY;
  }
  
  public int getX()
  {
    return iX;
  }

  public int getY()
  {
    return iY;
  }
  
  public int getDirectionX()
  {
    return iDirectionX;
  }

  public int getDirectionY()
  {
    return iDirectionY;
  }
}
