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

package Software.Animator.Support;

import java.awt.*;
import Software.Util.*;

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

  public synchronized void addNotify()
  {
    repaint();
    super.addNotify();
  }
}
