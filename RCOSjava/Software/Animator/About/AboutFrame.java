//**************************************************************************/
// FILE     : AboutFrame.java
// PACKAGE  : Animator.About
// PURPOSE  : To display the authors in all their glory.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/02/96  First created.
//          : 12/01/97  Updated it.
//          : 13/10/98  Converted to Java 1.1
//**************************************************************************/

package Software.Animator.About;

import java.awt.*;
import java.awt.event.*;
import RCOS;
import Software.Animator.RCOSFrame;
import Software.Animator.Support.GraphicButton;
import Software.Animator.Support.NewLabel;

public class AboutFrame extends RCOSFrame
{
  private Image[] myImages = new Image[RCOS.numberPeople];

  public AboutFrame (int x, int y, Image images[])
  {
    setTitle("About RCOS");
    myImages = images;
    setSize(x,y);
  }

  public void setupLayout(Component c)
  {
    super.setupLayout(c);
    Panel pMain = new Panel();
    Panel panelClose = new Panel();
		GraphicButton tmpGButton;
    NewLabel lTmpLabel;

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();
    constraints.gridwidth=1;
    constraints.gridheight=1;
    constraints.weighty=1;
    constraints.weightx=1;
    constraints.insets=new Insets(3,3,3,3);
    constraints.anchor = GridBagConstraints.CENTER;
    pMain.setLayout(gridBag);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel("RCOS.java Team.", titleFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pMain.add(lTmpLabel);

    constraints.gridwidth = 1;
    tmpGButton = new GraphicButton(myImages[0], myImages[0], "David Jones",
                   defaultFont, defaultFgColour, false, false);
    gridBag.setConstraints(tmpGButton,constraints);
    pMain.add(tmpGButton);

    Panel panel1 = new Panel();
    panel1.setLayout(new GridLayout(5,1));

    panel1.add(new NewLabel("Name: David Jones.   ", defaultFont, false));
    panel1.add(new NewLabel("Responsible for:     ", defaultFont, false));
    panel1.add(new NewLabel("- Kernel,            ", defaultFont, false));
    panel1.add(new NewLabel("- CPU, and           ", defaultFont, false));
    panel1.add(new NewLabel("- Hardware Terminals.", defaultFont, false));

    gridBag.setConstraints(panel1,constraints);
    pMain.add(panel1);

    tmpGButton = new GraphicButton(myImages[1], myImages[1], "Brett Carter",
                   defaultFont, buttonColour, false, false);

    gridBag.setConstraints(tmpGButton,constraints);
    pMain.add(tmpGButton);

    Panel panel2 = new Panel();
    panel2.setLayout(new GridLayout(5,1));

    panel2.add(new NewLabel("Name: Brett Carter.  ", defaultFont, false));
    panel2.add(new NewLabel("Responsible for:     ", defaultFont, false));
    panel2.add(new NewLabel("- Disk Schedular,    ", defaultFont, false));
    panel2.add(new NewLabel("- File System, and   ", defaultFont, false));
    panel2.add(new NewLabel("- File Server.       ", defaultFont, false));

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBag.setConstraints(panel2,constraints);
    pMain.add(panel2);

    tmpGButton = new GraphicButton(myImages[2], myImages[2], "Bruce Jamieson",
                   defaultFont, buttonColour, false, false);

    constraints.gridwidth = 1;
    gridBag.setConstraints(tmpGButton,constraints);
    pMain.add(tmpGButton);

    Panel panel3 = new Panel();
    panel3.setLayout(new GridLayout(5,1));

    panel3.add(new NewLabel("Name: Bruce Jamieson.", defaultFont, false));
    panel3.add(new NewLabel("Responsible for:     ", defaultFont, false));
    panel3.add(new NewLabel("- Memory Manager,    ", defaultFont, false));
    panel3.add(new NewLabel("- IPC Manager, and   ", defaultFont, false));
    panel3.add(new NewLabel("- Post Office.       ", defaultFont, false));

    gridBag.setConstraints(panel3,constraints);
    pMain.add(panel3);

    tmpGButton = new GraphicButton(myImages[3], myImages[3], "Andrew Newman",
                   defaultFont, buttonColour, false, false);

    gridBag.setConstraints(tmpGButton,constraints);
    pMain.add(tmpGButton);

    Panel panel4 = new Panel();
    panel4.setLayout(new GridLayout(6,1));
    panel4.add(new NewLabel("Name: Andrew Newman. ", defaultFont, false));
    panel4.add(new NewLabel("Responsible for:     ", defaultFont, false));
    panel4.add(new NewLabel("- User Interface,    ", defaultFont, false));
    panel4.add(new NewLabel("- Combinig RCOS elements,", defaultFont, false));
    panel4.add(new NewLabel("- Fixing bugs, and", defaultFont, false));
    panel4.add(new NewLabel("- Utility Objects.    ", defaultFont, false));

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBag.setConstraints(panel4,constraints);
    pMain.add(panel4);

    panelClose.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpButton = new Button("Close");
    panelClose.add(tmpButton);
    tmpButton.addMouseListener(new RCOSFrame.CloseAnimator());

    add("Center",pMain);
    add("South",panelClose);

    repaint();
  }
}
