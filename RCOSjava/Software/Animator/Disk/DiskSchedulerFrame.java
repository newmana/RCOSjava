//**************************************************************************/
// FILE     : About.java
// PACKAGE  : Animator.About
// PURPOSE  : To display the authors in all their glory.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/02/96  First created.
//          : 12/01/97  Updated it.
//          : 13/10/98  Converted to Java 1.1
//**************************************************************************/

package Software.Animator.Disk;

import java.awt.*;
import java.awt.event.*;
import Software.Animator.RCOSFrame;
import Software.Animator.Support.GraphicButton;
import Software.Animator.Support.NewLabel;

public class DiskSchedulerFrame extends RCOSFrame
{
  private Image[] myImages = new Image[6];

  public DiskSchedulerFrame (int x, int y, Image images[])
  {
    setTitle("About RCOS");
    myImages = images;
    setupLayout();
    setSize(x,y);
  }

  public void setupLayout()
  {
  }
}
