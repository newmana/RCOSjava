package org.rcosjava.hardware.terminal;
import java.awt.*;

import java.io.*;
import org.rcosjava.software.animator.RCOSFrame;

/**
 * Screen in which data is displayed. Basically emulating a *very* simple
 * graphics card. A character based 80x15 display system is emulated.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 23/03/96 Moved into package Terminal </DD>
 * <DD> 24/03/96 Modified to reverse membership. Terminal now extends Frame and
 * has a MessageHandler as a member. </DD>
 * <DD> 29/03/96 Separated into Hardware|SoftwareTerminal </DD>
 * <DD> 01/12/96 Some bugs to do with displaying on the screen are fixed. Text
 * nolonger has a box at the end and updating is done by rows instead of each
 * character. </DD>
 * <DD> 03/12/96 Double buffering for flicker free animation and you can set the
 * screen colours too. </DD>
 * <DD> 14/04/97 Split Hardware Terminal into HardwareTerminal and
 * HardwareTerminalScreen </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @created 24th January 1996
 * @see org.rcosjava.hardware.terminal.HardwareTerminal
 * @version 1.00 $Date$
 */
public class HardwareTerminalScreen extends Canvas
{
  /**
   * The maximum number of screen rows to display.
   */
  public final static int MAXROWS = 15;

  /**
   * The maximum number of columns for the screen to display.
   */
  public final static int MAXCOLS = 80;

  /**
   * The row in which to initially place the cursor.
   */
  public final static int STARTROWS = 1;

  /**
   * The column in which to initially place the cursor.
   */
  public final static int STARTCOLS = 1;

  /**
   * The row and column that the cursor is currently at.
   */
  private int row, col;

  /**
   * Whether the echo key presses to the screen.
   */
  private boolean echo;

  /**
   * The array of characters representing the screen display. Screen memory in
   * other words.
   */
  private char contents[];

  /**
   * The image buffer to write to for double buffering.
   */
  private Image imageBuffer;

  /**
   * The graphic system to write to.
   */
  private Graphics graphicsBuffer;

  /**
   * The size of the characters in pixels.
   */
  private int textHeight, textWidth;

  /**
   * The size in pixels of the graphics area (the screen).
   */
  private int width, height;

  /**
   * Creates a new terminal screen, initialising the screen memory (contents),
   * setting the size of the screen based on the size of the terminal.
   *
   * @param c the image producer in which to create the graphicBuffer.
   */
  public HardwareTerminalScreen(Component c)
  {
    super();
    contents = new char[MAXROWS * MAXCOLS];

    FontMetrics fm = getFontMetrics(RCOSFrame.terminlFont);

    textHeight = fm.getHeight();
    textWidth = fm.charWidth(' ');

    for (int count = 0; count < MAXROWS * MAXCOLS; count++)
    {
      contents[count] = ' ';
    }

    row = 0;
    col = 0;
    echo = true;
    width = (textWidth * MAXCOLS) + (2 * textWidth);
    height = (textHeight * MAXROWS) + (7 * textHeight);
    imageBuffer = c.createImage(width, height);
    graphicsBuffer = imageBuffer.getGraphics();
    setSize(width, height);
    repaint();
  }

  /**
   * @return the height of the screen in pixels.
   */
  public int getHeight()
  {
    return height;
  }

  /**
   * @return the width of the screen in pixels.
   */
  public int getWidth()
  {
    return width;
  }

  /**
   * Adds a feature to the Notify attribute of the HardwareTerminalScreen object
   */
  public void addNotify()
  {
    repaint();
    super.addNotify();
  }

  /**
   * Place a char or a short onto the terminal. Added the ability to ignore
   * carriage return (\r) and form feed (\f).
   *
   * @param ch the character to write on the screen.
   */
  public void printChr(char ch)
  {
    if ((ch != '\r') && (ch != '\f'))
    {
      if (ch != '\n')
      {
        if (echo)
        {
          contents[row * MAXCOLS + col] = ch;
          repaint();
        }

        col++;
        if (col == MAXCOLS)
        {
          scrollUp(1);
        }
      }
      else
      {
        scrollUp(1);
      }
    }
  }

  /**
   * Displays the number on the screen. Converts the number to a string,
   * determines its length and writes the characters to the screen.
   *
   * @param num the number to write to the screen.
   */
  public void printNum(short num)
  {
    StringBuffer tmpString = new
        StringBuffer((new String()).valueOf(num));
    int length = tmpString.length();
    int count;

    for (count = 0; count < length; count++)
    {
      printChr(tmpString.charAt(count));
    }
  }

  /**
   * Scroll display up specified number of rows
   *
   * @param numRows the number of rows to scroll the screen.
   */
  public void scrollUp(int numRows)
  {
    int cols;
    int rows;

    col = 0;
    row++;

    if (row == MAXROWS)
    {
      for (rows = 1; rows < MAXROWS; rows++)
      {
        for (cols = 0; cols < MAXCOLS; cols++)
        {
          contents[(rows - 1) * MAXCOLS + cols] = contents[rows * MAXCOLS + cols];
          if (rows == MAXROWS - 1)
          {
            contents[rows * MAXCOLS + cols] = ' ';
          }
        }
      }
      row--;
    }
    repaint();
  }

  /**
   * Calls update(g).
   *
   * @param g Description of Parameter
   */
  public void paint(Graphics g)
  {
    update(g);
  }

  /**
   * Refresh the screen.
   *
   * @param g Description of Parameter
   */
  public void update(Graphics g)
  {
    int cols;
    int rows;

    if (graphicsBuffer != null)
    {
      //Should use getFontMetrics but this is easier (and faster).

      graphicsBuffer.setFont(RCOSFrame.terminlFont);
      graphicsBuffer.setColor(RCOSFrame.defaultBgColour);
      graphicsBuffer.fillRect(0, 0, imageBuffer.getWidth(this),
          imageBuffer.getHeight(this));
      graphicsBuffer.setColor(RCOSFrame.terminalColour);

      // Prints out each line row by row.

      for (rows = 0; rows < MAXROWS; rows++)
      {
        graphicsBuffer.drawChars(contents, rows * MAXCOLS, MAXCOLS,
            textWidth * STARTCOLS, ((rows + 1) * textHeight) + (STARTROWS * textWidth));
      }
      g.drawImage(imageBuffer, 0, 0, this);
    }
  }
}
