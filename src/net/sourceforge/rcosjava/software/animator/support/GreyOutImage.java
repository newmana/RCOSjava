//**************************************************************************
// FILE     : GreyOutImage.java
// PACKAGE  : Animator
// PURPOSE  : Given an image make it grey.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/02/97  First created.
//
//  @see java.applet.Canvas for original class definition.
//  @author Andrew Newman
//  @version 01/02/1997, 1.00
//
//**************************************************************************/

package net.sourceforge.rcosjava.software.animator.support;

import java.awt.*;
import java.awt.image.*;

public class GreyOutImage extends RGBImageFilter
{
  public GreyOutImage()
  {
    canFilterIndexColorModel = true;
  }

  public int filterRGB(int iX, int iY, int iRGB)
  {
    DirectColorModel cm = (DirectColorModel) ColorModel.getRGBdefault();

    int alpha = cm.getAlpha(iRGB);
    int red = cm.getRed(iRGB);
    int green = cm.getGreen(iRGB);
    int blue = cm.getBlue(iRGB);

    red = Math.max((int) (red - (red * 0.4)), 0);
    green = Math.max((int) (green - (green * 0.4)), 0);
    blue = Math.max((int) (blue - (blue * 0.4)), 0);

    return alpha <<24 | red <<16 | green <<8 | blue;
  }
}
