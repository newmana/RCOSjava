package org.rcosjava.software.animator.about;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.rcosjava.RCOS;
import org.rcosjava.software.animator.RCOSFrame;
import org.rcosjava.software.animator.support.GraphicButton;
import org.rcosjava.software.animator.support.NewLabel;

/**
 * To display the authors in all their glory.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 12/01/97 Updated it. </DD>
 * <DD> 13/10/98 Converted to Java 1.1 </DD> </DT>
 * <P>
 * @author Andrew Newman
 * @created 1st February 1996
 * @version 1.00 $Date$
 */
public class AboutFrame extends RCOSFrame
{
  /**
   * Description of the Field
   */
  private Image[] myImages = new Image[RCOS.NUMBER_OF_PEOPLE];

  /**
   * Constructor for the AboutFrame object
   *
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param images Description of Parameter
   */
  public AboutFrame(int x, int y, ImageIcon images[])
  {
    setTitle("About RCOS");
    myImages = new Image[images.length];
    for (int index = 0; index < images.length; index++)
    {
      myImages[index] = images[index].getImage();
    }
    setSize(x, y);
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    Panel pMain = new Panel();
    Panel panelClose = new Panel();
    GraphicButton tmpGButton;
    NewLabel lTmpLabel;

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();

    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;
    constraints.insets = new Insets(3, 3, 3, 3);
    constraints.anchor = GridBagConstraints.CENTER;
    pMain.setLayout(gridBag);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel("RCOS.java Team.", titleFont);
    gridBag.setConstraints(lTmpLabel, constraints);
    pMain.add(lTmpLabel);

    constraints.gridwidth = 1;
    tmpGButton = new GraphicButton(myImages[0], myImages[0], "David Jones",
        defaultFont, defaultFgColour, false, false);
    gridBag.setConstraints(tmpGButton, constraints);
    pMain.add(tmpGButton);

    Panel panel1 = new Panel();

    panel1.setLayout(new GridLayout(5, 1));

    panel1.add(new NewLabel("Name: David Jones.   ", defaultFont, false));
    panel1.add(new NewLabel("Responsible for:     ", defaultFont, false));
    panel1.add(new NewLabel("- Kernel,            ", defaultFont, false));
    panel1.add(new NewLabel("- CPU, and           ", defaultFont, false));
    panel1.add(new NewLabel("- Hardware Terminals.", defaultFont, false));

    gridBag.setConstraints(panel1, constraints);
    pMain.add(panel1);

    tmpGButton = new GraphicButton(myImages[1], myImages[1], "Brett Carter",
        defaultFont, buttonColour, false, false);

    gridBag.setConstraints(tmpGButton, constraints);
    pMain.add(tmpGButton);

    Panel panel2 = new Panel();

    panel2.setLayout(new GridLayout(5, 1));

    panel2.add(new NewLabel("Name: Brett Carter.  ", defaultFont, false));
    panel2.add(new NewLabel("Responsible for:     ", defaultFont, false));
    panel2.add(new NewLabel("- Disk Schedular,    ", defaultFont, false));
    panel2.add(new NewLabel("- File System, and   ", defaultFont, false));
    panel2.add(new NewLabel("- File Server.       ", defaultFont, false));

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBag.setConstraints(panel2, constraints);
    pMain.add(panel2);

    tmpGButton = new GraphicButton(myImages[2], myImages[2], "Bruce Jamieson",
        defaultFont, buttonColour, false, false);

    constraints.gridwidth = 1;
    gridBag.setConstraints(tmpGButton, constraints);
    pMain.add(tmpGButton);

    Panel panel3 = new Panel();

    panel3.setLayout(new GridLayout(5, 1));

    panel3.add(new NewLabel("Name: Bruce Jamieson.", defaultFont, false));
    panel3.add(new NewLabel("Responsible for:     ", defaultFont, false));
    panel3.add(new NewLabel("- Memory Manager,    ", defaultFont, false));
    panel3.add(new NewLabel("- IPC Manager, and   ", defaultFont, false));
    panel3.add(new NewLabel("- Post Office.       ", defaultFont, false));

    gridBag.setConstraints(panel3, constraints);
    pMain.add(panel3);

    tmpGButton = new GraphicButton(myImages[3], myImages[3], "Andrew Newman",
        defaultFont, buttonColour, false, false);

    gridBag.setConstraints(tmpGButton, constraints);
    pMain.add(tmpGButton);

    Panel panel4 = new Panel();

    panel4.setLayout(new GridLayout(6, 1));
    panel4.add(new NewLabel("Name: Andrew Newman. ", defaultFont, false));
    panel4.add(new NewLabel("Responsible for:     ", defaultFont, false));
    panel4.add(new NewLabel("- User Interface,    ", defaultFont, false));
    panel4.add(new NewLabel("- Combinig RCOS elements,", defaultFont, false));
    panel4.add(new NewLabel("- Fixing bugs, and", defaultFont, false));
    panel4.add(new NewLabel("- Utility Objects.    ", defaultFont, false));

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBag.setConstraints(panel4, constraints);
    pMain.add(panel4);

    panelClose.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpButton = new JButton("Close");
    panelClose.add(tmpButton);
    tmpButton.addMouseListener(new RCOSFrame.CloseAnimator());

    getContentPane().add("Center", pMain);
    getContentPane().add("South", panelClose);

    repaint();
  }
}
