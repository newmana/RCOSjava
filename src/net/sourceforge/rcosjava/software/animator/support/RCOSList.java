// *************************************************************************
// FILE     : RCOSList.java
// PACKAGE  : Animator
// PURPOSE  : Extends the list AWT component to enable double click to
//            pass to the parent.
// AUTHOR   : Brett Carter
// MODIFIED : Andrew Newman
// HISTORY  : 1/2/96  Created.
//            1/1/97  Moved to Animator package.
//
// *************************************************************************

package net.sourceforge.rcosjava.software.animator.support;

import java.awt.*;
import net.sourceforge.rcosjava.software.util.*;

public class RCOSList extends List
{
  //Variable to hold parent class.

  public Component cParentComponent;
  private Font myFont = new Font ("TimesRoman", Font.PLAIN, 12);

  //Constructor to include passing the new parent.

  public RCOSList(Component cParent)
  {
    super();
    setBackground(Color.black);
    setForeground(Color.white);
    setFont(myFont);
    cParentComponent = cParent;
  }

  //Constructor to include passing the new parent.

  public RCOSList(Component cParent, int iOne, boolean bTwo)
  {
    super(iOne, bTwo);
    setBackground(Color.black);
    setForeground(Color.white);
    setFont(myFont);
    cParentComponent = cParent;
  }

  //Constructor to include passing the new parent and font.

  public RCOSList(Component cParent, Font theFont)
  {
    super();
    setBackground(Color.black);
    setForeground(Color.white);
    cParentComponent = cParent;
    myFont = theFont;
    setFont(myFont);
  }

  //Constructor to include passing the new parent and font

  public RCOSList(Component cParent, int iOne, boolean bTwo, Font theFont)
  {
    super(iOne, bTwo);
    setBackground(Color.black);
    setForeground(Color.white);
    cParentComponent = cParent;
    myFont = theFont;
    setFont(myFont);
  }

  public void addNotify()
  {
    repaint();
    super.addNotify();
  }
}
