package org.rcosjava.software.animator.cpu;

import java.awt.*;
import javax.swing.ImageIcon;
import org.rcosjava.hardware.cpu.Context;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.software.animator.RCOSAnimator;

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
   * Description of the Field
   */
  private static Context theContext;
  /**
   * Description of the Field
   */
  private final static String MESSENGING_ID = "CPUAnimator";
  /**
   * Description of the Field
   */
  private CPUFrame cpuFrame;

  /**
   * Constructor for the CPUAnimator object
   *
   * @param postOffice Description of Parameter
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param cpuImages Description of Parameter
   */
  public CPUAnimator(AnimatorOffice postOffice, int x, int y,
      ImageIcon[] cpuImages)
  {
    super(MESSENGING_ID, postOffice);
    theContext = new Context();
    cpuFrame = new CPUFrame(x, y, cpuImages, this);
    cpuFrame.pack();
    cpuFrame.setSize(x, y);
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    cpuFrame.setupLayout(c);
  }

  /**
   * Sets the Context attribute of the CPUAnimator object
   *
   * @param newContext The new Context value
   */
  public void setContext(Context newContext)
  {
    theContext = newContext;
    cpuFrame.setContext();
  }

  /**
   * Gets the Context attribute of the CPUAnimator object
   *
   * @return The Context value
   */
  public Context getContext()
  {
    return theContext;
  }

  /**
   * Description of the Method
   */
  public void disposeFrame()
  {
    cpuFrame.dispose();
  }

  /**
   * Description of the Method
   */
  public void showFrame()
  {
    cpuFrame.setVisible(true);
  }

  /**
   * Description of the Method
   */
  public void hideFrame()
  {
    cpuFrame.setVisible(false);
  }

  /**
   * Description of the Method
   */
  public void showCPU()
  {
    cpuFrame.showCPU();
  }

  /**
   * Description of the Method
   *
   * @param newStack Description of Parameter
   */
  public void updateStack(Memory newStack)
  {
    cpuFrame.updateStack(newStack);
    cpuFrame.updateCode();
  }

  /**
   * Description of the Method
   *
   * @param newCode Description of Parameter
   */
  public void loadCode(Memory newCode)
  {
    try
    {
      cpuFrame.loadCode(newCode);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Description of the Method
   */
  public void screenReset()
  {
    cpuFrame.screenReset();
  }
}
