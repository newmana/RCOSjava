//**************************************************************************/
// FILE     : FileSystemFrame.java
// PACKAGE  : Animator.About
// PURPOSE  : To display the authors in all their glory.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/02/96  First created.
//          : 12/01/97  Updated it.
//          : 13/10/98  Converted to Java 1.1
//**************************************************************************/

package net.sourceforge.rcosjava.software.animator.filesystem;

import java.awt.*;
import java.awt.event.*;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.animator.support.GraphicButton;
import net.sourceforge.rcosjava.software.animator.support.NewLabel;

public class FileSystemFrame extends RCOSFrame
{
  private Image[] myImages = new Image[6];

  public FileSystemFrame (int x, int y, Image images[])
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
