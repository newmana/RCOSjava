// *************************************************************************
// FILE     : RCOSBox.java
// PACKAGE  : Animator
// PURPOSE  : Based on the box class found in "Graphic Java, Mastering the
//            AWT".
//
// AUTHOR   : David M. Geary, Allan L. McClellan
// MODIFIED : Andrew Newman
// HISTORY  : 1/7/97  Created.
//
// *************************************************************************

package net.sourceforge.rcosjava.software.animator.support;

import java.awt.*;
import net.sourceforge.rcosjava.software.animator.support.mtgos.MTGO;
import net.sourceforge.rcosjava.software.animator.support.positions.Position;

public class RCOSBox extends Panel
{
  private NewLabel lTitle;
  private int iTop, iLeft, iBottom, iRight, iTitleTop;
  private Color bgColour = Color.black;
  private Color fgColour = Color.white;

  public RCOSBox(Component surrounded, String title)
  {
    this(surrounded, new NewLabel(title, surrounded.getFont()));
  }

  public RCOSBox(Component surrounded, String title, Color bColour,
                 Color fColour)
  {
    this(surrounded, new NewLabel(title, surrounded.getFont()));
    bgColour = bColour;
    fgColour = fColour;
  }

  public RCOSBox(Component surrounded, NewLabel label)
  {
    lTitle = label;

    iTop = 0;
    iLeft = 1;
    iBottom = 1;
    iRight = 1;
    iTitleTop = 0;

    setBackground(bgColour);
    setForeground(fgColour);

    setupLayout(surrounded, label);
  }

  public RCOSBox(Component surrounded, Color bColour, Color fColour, int Top, int Left,
                 int Bottom, int Right)
  {
    iTop = Top;
    iLeft = Left;
    iBottom = Bottom;
    iRight = Right;

    bgColour = bColour;
    fgColour = fColour;
    setBackground(bgColour);
    setForeground(fgColour);
    NewLabel label = new NewLabel("", new Font ("TimesRoman", Font.PLAIN, 12));

    setupLayout(surrounded, label);
  }

  public RCOSBox(Component surrounded, NewLabel label, int Top, int Left,
                 int Bottom, int Right)
  {
    lTitle = label;

    iTop = Top;
    iLeft = Left;
    iBottom = Bottom;
    iRight = Right;

    setBackground(bgColour);
    setForeground(fgColour);

    setupLayout(surrounded, label);
  }

  public void setupLayout(Component surrounded, NewLabel label)
  {
    GridBagLayout gbLayout = new GridBagLayout();
    GridBagConstraints gbConstraints = new GridBagConstraints();

    setLayout(gbLayout);

    if (label.getText() != "")
    {
      gbConstraints.gridwidth = GridBagConstraints.REMAINDER;
      gbConstraints.anchor = GridBagConstraints.NORTH;
      gbLayout.setConstraints(lTitle, gbConstraints);

      add(lTitle);
    }

    gbConstraints.anchor = GridBagConstraints.CENTER;
    gbConstraints.weighty = 1.0;
    gbConstraints.weightx = 1.0;
    gbConstraints.fill = GridBagConstraints.BOTH;
    gbConstraints.insets = new Insets(iTop,iLeft,iBottom,iRight);
    gbLayout.setConstraints(surrounded,gbConstraints);
    add(surrounded);
  }

  public synchronized void addNotify()
  {
    repaint();
    super.addNotify();
  }

  public void paint(Graphics g)
  {
    update(g);
  }

  public void update(Graphics g)
  {
    FontMetrics fm = lTitle.getFontMetrics(lTitle.getFont());

    int top = getInsets().top + fm.getAscent();
    Dimension size = getSize();
    g.draw3DRect(0,top,size.width-1,size.height-top-1,false);
  }

  public void setBounds(int iW, int iH)
  {
	  setBounds(getLocation().x, getLocation().y, iW, iH);
  }

  public void setBounds(int iX, int iY, int iW, int iH)
  {
    super.setBounds(iX,iY,iW,iH);
  }
}
