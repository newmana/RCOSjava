//***************************************************************************
// FILE    : HardwareTerminalScreen.java
// PURPOSE : Screen in which data is displayed.  Basically emulating
//           a *very* simple graphics card.
// AUTHOR  : Andrew Newman (based on code by David Jones)
// HISTORY : 24/01/96  Created
//           23/03/96  Moved into package Terminal
//           24/03/96  Modified to reverse membership
//                     Terminal now extends Frame and
//                     has a MessageHandler as a member
//           29/03/96  Separated into Hardware|SoftwareTerminal
//           01/12/96  Some bugs to do with displaying on the
//                     screen are fixed.  Text nolonger has
//                     a box at the end and updating is done
//                     by rows instead of each character.
//           03/12/96  Double buffering for flicker free animation
//                     and you can set the screen colours too.
//           14/04/97  Split Hardware Terminal into HardwareTerminal and
//                     and HardwareTerminalScreen
//
//***************************************************************************/

package Hardware.Terminal;

import java.io.*;
import java.awt.*;
import Software.Animator.RCOSFrame;
import Software.Util.FIFOQueue;

public class HardwareTerminalScreen extends Canvas
{
  public static final int MAXROWS = 15;
  public static final int MAXCOLS = 80;
  public static final int STARTROWS = 1;
  public static final int STARTCOLS = 1;

  private int row, col;
  private boolean echo;
  private char contents[];

  private Image iImageBuffer;
  private Graphics gGraphicsBuffer;
  private int iTextHeight, iTextWidth;
  private int iWidth, iHeight;

  public HardwareTerminalScreen (Component c)
  {
  	super();
    contents = new char[MAXROWS*MAXCOLS];
    FontMetrics fm = getFontMetrics(RCOSFrame.fTerminlFont);
    iTextHeight = fm.getHeight();
    iTextWidth = fm.charWidth(' ');

    for (int count=0; count < MAXROWS*MAXCOLS; count++)
      contents[count] = ' ';

    row = 0;
    col = 0;
    echo = true;
    iWidth =(iTextWidth*MAXCOLS)+(2*iTextWidth);
    iHeight =(iTextHeight*MAXROWS)+(7*iTextHeight);
    iImageBuffer = c.createImage(iWidth,iHeight);
    gGraphicsBuffer = iImageBuffer.getGraphics();
    setSize(iWidth,iHeight);
    repaint();
  }

  public int getHeight()
  {
    return iHeight;
  }

  public int getWidth()
  {
    return iWidth;
  }

  public synchronized void addNotify()
  {
    repaint();
    super.addNotify();
  }

  // place a char or a short onto the terminal
  // - added the ability to ignore carriage return (\r) and
  //   form feed (\f)

  public void printChr (char ch)
  {
    if ((ch != '\r') && (ch != '\f'))
    {
      if (ch != '\n')
      {
        if (echo)
        {
          contents[row*MAXCOLS + col] = ch;
          repaint();
        }

        col++;
        if (col == MAXCOLS)
          scrollUp(1);
      }
      else
      {
        scrollUp(1);
      }
    }
  }

  public void printNum (short num)
  {
    StringBuffer tmpString = new
      StringBuffer((new String()).valueOf(num));
    int length = tmpString.length(), count;
    for (count = 0; count < length; count++)
      printChr(tmpString.charAt(count));
  }

  // scroll display up specified number of rows

  public void scrollUp (int numRows)
  {
    int cols, rows;

    col=0;
    row++;

    if (row == MAXROWS)
    {
      for (rows=1; rows<MAXROWS; rows++)
      {
        for (cols=0; cols<MAXCOLS; cols++)
        {
          contents[(rows-1)*MAXCOLS+cols] = contents[rows*MAXCOLS+cols];
          if (rows == MAXROWS-1)
            contents[rows*MAXCOLS+cols] = ' ';
        }
      }
      row--;
    }
    repaint();
  }

  public void paint (Graphics g)
  {
    update(g);
  }

  // refresh the screen

  public void update (Graphics g)
  {
    int cols, rows;

    if (gGraphicsBuffer != null)
    {
      //Should use getFontMetrics but this is easier (and faster).

      gGraphicsBuffer.setFont(RCOSFrame.fTerminlFont);
      gGraphicsBuffer.setColor(RCOSFrame.cDefaultBgColour);
      gGraphicsBuffer.fillRect(0,0,iImageBuffer.getWidth(this),
                               iImageBuffer.getHeight(this));
      gGraphicsBuffer.setColor(RCOSFrame.cTerminalColour);

      // Prints out each line row by row.

      for (rows=0; rows < MAXROWS; rows++)
      {
        gGraphicsBuffer.drawChars(contents, rows*MAXCOLS, MAXCOLS,
        iTextWidth*STARTCOLS, ((rows+1)*iTextHeight)+(STARTROWS*iTextWidth));
      }
      g.drawImage(iImageBuffer,0,0,this);
    }
  }
}