package org.rcosjava.software.animator.cpu;

import java.awt.*;
import javax.swing.ImageIcon;
import org.rcosjava.hardware.cpu.Context;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.software.animator.RCOSAnimator;
import org.rcosjava.software.animator.RCOSPanel;

/**
 * Sends and receives messages for the graphical representation of the P-Code
 * CPU.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 28/01/96 Modifying to become CPUAnimator </DD>
 * <DD> 23/03/96 Moved into CPU package </DD>
 * <DD> 02/02/97 Moved to Animator Package </DD>
 * <DD> 03/02/97 Seperated between Message Handler and Frame. </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @created 24th January 1996
 * @version 1.00 $Date$
 */
public class CPUAnimator extends RCOSAnimator
{
  /**
   * The CPU context to display.
   */
  private static Context theContext;

  /**
   * What to register to the animator post office as.
   */
  private final static String MESSENGING_ID = "CPUAnimator";

  /**
   * Panel to display results in.
   */
  private CPUPanel panel;

  /**
   * Constructor for the CPUAnimator object
   *
   * @param postOffice the post office to register to.
   */
  public CPUAnimator(AnimatorOffice postOffice)
  {
    super(MESSENGING_ID, postOffice);
    theContext = new Context();
    panel = new CPUPanel(this);
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    panel.setupLayout(c);
  }

  /**
   * Sets the context (Program Counter, Base Pointer, etc).
   *
   * @param newContext the new context.
   */
  public void setContext(Context newContext)
  {
    theContext = newContext;
    panel.setContext();
  }

  /**
   * Returns the current context (Program Counter, etc).
   *
   * @return the current context (Program Counter, etc).
   */
  public Context getContext()
  {
    return theContext;
  }

  /**
   * Returns the panel of this component.
   *
   * @return the panel of this component.
   */
  public RCOSPanel getPanel()
  {
    return panel;
  }

  /**
   * Update the stack of the currently executing process.
   *
   * @param newStack the new value of the stack.
   */
  public void updateStack(Memory newStack)
  {
    panel.updateStack(newStack);
    panel.updateCode();
  }

  /**
   * Update the code of the current executing process.
   *
   * @param newCode the new value of the code.
   */
  public void loadCode(Memory newCode)
  {
    try
    {
      panel.loadCode(newCode);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Resets the display of the stack, code and context.
   */
  public void screenReset()
  {
    panel.screenReset();
  }
}
