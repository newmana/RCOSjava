//**************************************************************************
// FILE     : Help.java
// PACKAGE  : Animator
// PURPOSE  : Bring up the HTML help in a web browser.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 14/02/96  First created.
//
//**************************************************************************/

package net.sourceforge.rcosjava.software.animator.help;

import java.applet.*;
import java.awt.*;
import java.net.*;

public class Help
{
  public Help (AppletContext myContext, String myURL, String where)
  {
    try
    {
      URL helpURL = new URL(myURL + where);
      myContext.showDocument(helpURL,"RCOS_Online_Help_Window");
    }
    catch (MalformedURLException e)
    {
    }
  }
}
