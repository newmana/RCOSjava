package org.rcosjava.software.animator.support;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;

/**
 * A simple
 *
 * @author administrator
 * @created 28 April 2002
 */
public class RCOSBox extends JPanel
{
  /**
   * Description of the Field
   */
  private JLabel title;
  /**
   * Description of the Field
   */
  private int iTop, iLeft, iBottom, iRight;
  /**
   * Description of the Field
   */
  private Color bgColour = Color.black;
  /**
   * Description of the Field
   */
  private Color fgColour = Color.white;

  /**
   * Constructor for the RCOSBox object
   *
   * @param surrounded Description of Parameter
   * @param title Description of Parameter
   */
  public RCOSBox(Component surrounded, String title)
  {
    this(surrounded, new JLabel(title));
  }

  /**
   * Constructor for the RCOSBox object
   *
   * @param surrounded Description of Parameter
   * @param title Description of Parameter
   * @param bColour Description of Parameter
   * @param fColour Description of Parameter
   */
  public RCOSBox(Component surrounded, String title, Color bColour,
      Color fColour)
  {
    this(surrounded, new JLabel(title));
    bgColour = bColour;
    fgColour = fColour;
  }

  /**
   * Constructor for the RCOSBox object
   *
   * @param surrounded Description of Parameter
   * @param label Description of Parameter
   */
  public RCOSBox(Component surrounded, JLabel label)
  {
    title = label;

    iTop = 0;
    iLeft = 1;
    iBottom = 1;
    iRight = 1;

    setBackground(bgColour);
    setForeground(fgColour);

    setupLayout(surrounded, label);
  }

  /**
   * Constructor for the RCOSBox object
   *
   * @param surrounded Description of Parameter
   * @param bColour Description of Parameter
   * @param fColour Description of Parameter
   * @param Top Description of Parameter
   * @param Left Description of Parameter
   * @param Bottom Description of Parameter
   * @param Right Description of Parameter
   */
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

    JLabel label = new JLabel("");
    label.setFont(new Font("TimesRoman", Font.PLAIN, 12));

    setupLayout(surrounded, label);
  }

  /**
   * Constructor for the RCOSBox object
   *
   * @param surrounded Description of Parameter
   * @param label Description of Parameter
   * @param Top Description of Parameter
   * @param Left Description of Parameter
   * @param Bottom Description of Parameter
   * @param Right Description of Parameter
   */
  public RCOSBox(Component surrounded, JLabel label, int Top, int Left,
      int Bottom, int Right)
  {
    title = label;

    iTop = Top;
    iLeft = Left;
    iBottom = Bottom;
    iRight = Right;

    setBackground(bgColour);
    setForeground(fgColour);

    setupLayout(surrounded, label);
  }

  /**
   * Description of the Method
   *
   * @param surrounded Description of Parameter
   * @param label Description of Parameter
   */
  public void setupLayout(Component surrounded, JLabel label)
  {
    GridBagLayout gbLayout = new GridBagLayout();
    GridBagConstraints gbConstraints = new GridBagConstraints();

    setLayout(gbLayout);

    if (label.getText() != "")
    {
      gbConstraints.gridwidth = GridBagConstraints.REMAINDER;
      gbConstraints.anchor = GridBagConstraints.NORTH;
      gbLayout.setConstraints(title, gbConstraints);

      add(title);
    }

    gbConstraints.anchor = GridBagConstraints.CENTER;
    gbConstraints.weighty = 1.0;
    gbConstraints.weightx = 1.0;
    gbConstraints.fill = GridBagConstraints.BOTH;
    gbConstraints.insets = new Insets(iTop, iLeft, iBottom, iRight);
    gbLayout.setConstraints(surrounded, gbConstraints);
    add(surrounded);
  }

  /**
   * Sets the Bounds attribute of the RCOSBox object
   *
   * @param iW The new Bounds value
   * @param iH The new Bounds value
   */
  public void setBounds(int iW, int iH)
  {
    setBounds(getLocation().x, getLocation().y, iW, iH);
  }

  /**
   * Sets the Bounds attribute of the RCOSBox object
   *
   * @param iX The new Bounds value
   * @param iY The new Bounds value
   * @param iW The new Bounds value
   * @param iH The new Bounds value
   */
  public void setBounds(int iX, int iY, int iW, int iH)
  {
    super.setBounds(iX, iY, iW, iH);
  }

  /**
   * Adds a feature to the Notify attribute of the RCOSBox object
   */
  public void addNotify()
  {
    repaint();
    super.addNotify();
  }

  /**
   * Description of the Method
   *
   * @param g Description of Parameter
   */
  public void paint(Graphics g)
  {
    update(g);
  }

  /**
   * Description of the Method
   *
   * @param g Description of Parameter
   */
  public void update(Graphics g)
  {
    FontMetrics fm = title.getFontMetrics(title.getFont());

    int top = getInsets().top + fm.getAscent();
    Dimension size = getSize();

    g.draw3DRect(0, top, size.width - 1, size.height - top - 1, false);
  }
}
