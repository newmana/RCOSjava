package org.rcosjava.software.animator.support;import java.applet.*;import java.awt.*;import java.net.*;import java.util.*;/** * Description of the Class * * @author administrator * @created 28 April 2002 */public class NewLabel extends Canvas{  /**   * Description of the Field   */  public String sMyString;  /**   * Description of the Field   */  public Font fMyFont;  /**   * Description of the Field   */  private boolean bCentered;  /**   * Constructor for the NewLabel object   *   * @param sString Description of Parameter   * @param fFont Description of Parameter   */  public NewLabel(String sString, Font fFont)  {    super();    sMyString = sString;    fMyFont = fFont;    bCentered = true;    repaint();  }  /**   * Constructor for the NewLabel object   *   * @param sString Description of Parameter   * @param fFont Description of Parameter   * @param bCenter Description of Parameter   */  public NewLabel(String sString, Font fFont, boolean bCenter)  {    super();    sMyString = sString;    fMyFont = fFont;    bCentered = bCenter;    repaint();  }  /**   * Sets the Text attribute of the NewLabel object   *   * @param sString The new Text value   */  public void setText(String sString)  {    sMyString = sString;    repaint();  }  /**   * Gets the Text attribute of the NewLabel object   *   * @return The Text value   */  public String getText()  {    return sMyString;  }  /**   * Gets the MinimumSize attribute of the NewLabel object   *   * @return The MinimumSize value   */  public Dimension getMinimumSize()  {    return getPreferredSize();  }  /**   * Gets the PreferredSize attribute of the NewLabel object   *   * @return The PreferredSize value   */  public Dimension getPreferredSize()  {    FontMetrics fm = getFontMetrics(fMyFont);    return new Dimension((fm.stringWidth(sMyString) + 5), (fm.getHeight() + 5));  }  /**   * Adds a feature to the Notify attribute of the NewLabel object   */  public void addNotify()  {    repaint();    super.addNotify();  }  /**   * Description of the Method   *   * @param g Description of Parameter   */  public void paint(Graphics g)  {    update(g);  }  /**   * Description of the Method   *   * @param g Description of Parameter   */  public void update(Graphics g)  {    Dimension size = this.getSize();    FontMetrics fm = getFontMetrics(fMyFont);    g.setColor(Color.black);    g.fillRect(0, 0, size.width, size.height);    g.setColor(Color.white);    g.setFont(fMyFont);    if (bCentered)    {      int x = (size.width / 2) - (fm.stringWidth(sMyString) / 2);      int y = (size.height / 2) + (fm.getAscent() / 2);      g.drawString(sMyString, x, y);    }    else    {      g.drawString(sMyString, 0, fm.getAscent());    }  }}