package org.rcosjava.software.animator.about;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.rcosjava.RCOS;
import org.rcosjava.software.animator.RCOSFrame;
import org.rcosjava.software.animator.support.GraphicButton;

/**
 * To display the authors in all their glory.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 12/01/97 Updated it. </DD>
 * <DD> 13/10/98 Converted to Java 1.1 </DD>
 * <DD> 24/07/2003 Added Danielly.</DD> </DT>
 * <P>
 * @author Andrew Newman
 * @created 1st February 1996
 * @version 1.00 $Date$
 */
public class AboutFrame extends RCOSFrame
{
  /**
   * Constructor for the AboutFrame object
   *
   * @param x width of the frame.
   * @param y height of the frame.
   * @param images pictures of the authors.
   */
  public AboutFrame(int x, int y)
  {
    setTitle("About RCOSjava");
    setSize(x, y);
  }

  /**
   * Creates a panel for the title, a panel for the authors and a panel for the
   * close button and adds them to the content pane.
   *
   * @param c the parent component.
   */
  public void setupLayout(Component c)
  {
    JPanel titlePanel = new JPanel();
    titlePanel.setBackground(DEFAULT_BG_COLOUR);
    titlePanel.setForeground(DEFAULT_FG_COLOUR);

    JPanel contentPanel = new JPanel();
    contentPanel.setBackground(DEFAULT_BG_COLOUR);
    contentPanel.setForeground(DEFAULT_FG_COLOUR);
    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;
    constraints.insets = new Insets(3, 3, 3, 3);
    constraints.anchor = GridBagConstraints.WEST;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    contentPanel.setLayout(gridBag);

    JPanel closePanel = new JPanel();
    closePanel.setBackground(DEFAULT_BG_COLOUR);
    closePanel.setForeground(DEFAULT_FG_COLOUR);

    JLabel tmpLabel;

    tmpLabel = new JLabel("About RCOSjava");
    tmpLabel.setFont(titleFont);
    tmpLabel.setBackground(DEFAULT_BG_COLOUR);
    tmpLabel.setForeground(DEFAULT_FG_COLOUR);
    titlePanel.add(tmpLabel);

    JPanel tmpPanel = new JPanel();
    tmpPanel.setBackground(DEFAULT_BG_COLOUR);
    tmpPanel.setForeground(DEFAULT_FG_COLOUR);
    tmpLabel = new JLabel("Version 0.5");
    tmpLabel.setBackground(DEFAULT_BG_COLOUR);
    tmpLabel.setForeground(DEFAULT_FG_COLOUR);
    tmpPanel.add(tmpLabel);
    gridBag.setConstraints(tmpPanel, constraints);
    contentPanel.add(tmpPanel);

    tmpPanel = new JPanel();
    tmpPanel.setBackground(DEFAULT_BG_COLOUR);
    tmpPanel.setForeground(DEFAULT_FG_COLOUR);
    tmpLabel = new JLabel("Licensed under the GPL (GNU Public Licence)");
    tmpLabel.setBackground(DEFAULT_BG_COLOUR);
    tmpLabel.setForeground(DEFAULT_FG_COLOUR);
    tmpPanel.add(tmpLabel);
    gridBag.setConstraints(tmpPanel, constraints);
    contentPanel.add(tmpPanel);

    tmpPanel = new JPanel();
    tmpPanel.setBackground(DEFAULT_BG_COLOUR);
    tmpPanel.setForeground(DEFAULT_FG_COLOUR);
    tmpLabel = new JLabel("Copyright 1997-2003");
    tmpLabel.setBackground(DEFAULT_BG_COLOUR);
    tmpLabel.setForeground(DEFAULT_FG_COLOUR);
    tmpPanel.add(tmpLabel);
    gridBag.setConstraints(tmpPanel, constraints);
    contentPanel.add(tmpPanel);

    tmpPanel = new JPanel();
    tmpPanel.setBackground(DEFAULT_BG_COLOUR);
    tmpPanel.setForeground(DEFAULT_FG_COLOUR);
    tmpLabel = new JLabel("Current Contributors:");
    tmpLabel.setBackground(DEFAULT_BG_COLOUR);
    tmpLabel.setForeground(DEFAULT_FG_COLOUR);
    tmpPanel.add(tmpLabel);
    gridBag.setConstraints(tmpPanel, constraints);
    contentPanel.add(tmpPanel);

    tmpPanel = new JPanel(new GridLayout(2,1));
    tmpPanel.setBackground(DEFAULT_BG_COLOUR);
    tmpPanel.setForeground(DEFAULT_FG_COLOUR);
    tmpLabel = new JLabel("Danielly Karine");
    tmpLabel.setBackground(DEFAULT_BG_COLOUR);
    tmpLabel.setForeground(DEFAULT_FG_COLOUR);
    tmpLabel.setFont(defaultFont);
    tmpPanel.add(tmpLabel);
    tmpLabel = new JLabel("Andrew Newman");
    tmpLabel.setBackground(DEFAULT_BG_COLOUR);
    tmpLabel.setForeground(DEFAULT_FG_COLOUR);
    tmpLabel.setFont(defaultFont);
    tmpPanel.add(tmpLabel);
    constraints.anchor = GridBagConstraints.CENTER;
    gridBag.setConstraints(tmpPanel, constraints);
    contentPanel.add(tmpPanel);

    tmpPanel = new JPanel();
    tmpPanel.setBackground(DEFAULT_BG_COLOUR);
    tmpPanel.setForeground(DEFAULT_FG_COLOUR);
    tmpLabel = new JLabel("Original Contributors:");
    tmpLabel.setBackground(DEFAULT_BG_COLOUR);
    tmpLabel.setForeground(DEFAULT_FG_COLOUR);
    tmpPanel.add(tmpLabel);
    constraints.anchor = GridBagConstraints.WEST;
    gridBag.setConstraints(tmpPanel, constraints);
    contentPanel.add(tmpPanel);

    tmpPanel = new JPanel(new GridLayout(4,1));
    tmpPanel.setBackground(DEFAULT_BG_COLOUR);
    tmpPanel.setForeground(DEFAULT_FG_COLOUR);
    tmpLabel = new JLabel("Brett Carter");
    tmpLabel.setBackground(DEFAULT_BG_COLOUR);
    tmpLabel.setForeground(DEFAULT_FG_COLOUR);
    tmpLabel.setFont(defaultFont);
    tmpPanel.add(tmpLabel);
    tmpLabel = new JLabel("Bruce Jamieson");
    tmpLabel.setBackground(DEFAULT_BG_COLOUR);
    tmpLabel.setForeground(DEFAULT_FG_COLOUR);
    tmpLabel.setFont(defaultFont);
    tmpPanel.add(tmpLabel);
    tmpLabel = new JLabel("David Jones");
    tmpLabel.setBackground(DEFAULT_BG_COLOUR);
    tmpLabel.setForeground(DEFAULT_FG_COLOUR);
    tmpLabel.setFont(defaultFont);
    tmpPanel.add(tmpLabel);
    tmpLabel = new JLabel("Andrew Newman");
    tmpLabel.setBackground(DEFAULT_BG_COLOUR);
    tmpLabel.setForeground(DEFAULT_FG_COLOUR);
    tmpLabel.setFont(defaultFont);
    tmpPanel.add(tmpLabel);
    constraints.anchor = GridBagConstraints.CENTER;
    gridBag.setConstraints(tmpPanel, constraints);
    contentPanel.add(tmpPanel);

    closePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    closePanel.setBackground(DEFAULT_BG_COLOUR);
    closePanel.setForeground(DEFAULT_FG_COLOUR);
    tmpButton = new JButton("Close");
    closePanel.add(tmpButton);
    tmpButton.addMouseListener(new RCOSFrame.CloseAnimator());

    getContentPane().add("North", titlePanel);
    getContentPane().add("Center", contentPanel);
    getContentPane().add("South", closePanel);
  }
}
