package net.sourceforge.rcosjava.software.animator.cpu;

import java.awt.*;
import net.sourceforge.rcosjava.software.animator.RCOSAnimator;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.hardware.cpu.Context;
import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import net.sourceforge.rcosjava.software.memory.MemoryRequest;

/**
 * Sends and receives messages for the graphical representation of the P-Code
 * CPU.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 28/01/96  Modifying to become CPUAnimator
 * </DD><DD>
 * 23/03/96  Moved into CPU package
 * </DD><DD>
 * 02/02/97  Moved to Animator Package
 * </DD><DD>
 * 03/02/97  Seperated between Message Handler and Frame.
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @version 1.00 $Date$
 * @created 24th January 1996
 */
public class CPUAnimator extends RCOSAnimator
{
  private static Context theContext;
  private CPUFrame cpuFrame;
  private static final String MESSENGING_ID = "CPUAnimator";

  public CPUAnimator(AnimatorOffice aPostOffice, int x, int y,
    Image[] cpuImages)
  {
    super(MESSENGING_ID, aPostOffice);
    theContext = new Context();
    cpuFrame = new CPUFrame(x, y, cpuImages, this);
    cpuFrame.pack();
    cpuFrame.setSize(x,y);
  }

  public void setupLayout(Component c)
  {
    cpuFrame.setupLayout(c);
  }

  public void disposeFrame()
  {
    cpuFrame.dispose();
  }

  public void showFrame()
  {
    cpuFrame.setVisible(true);
  }

  public void hideFrame()
  {
    cpuFrame.setVisible(false);
  }

  public void setContext(Context cNewContext)
  {
    theContext = cNewContext;
    cpuFrame.setContext();
  }

  public Context getContext()
  {
    return theContext;
  }

  public void showCPU()
  {
    cpuFrame.showCPU();
  }

  public void updateStack(Memory newStack)
  {
    cpuFrame.updateStack(newStack);
    cpuFrame.updateCode();
  }

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

  public void screenReset()
  {
    cpuFrame.screenReset();
  }

  public void processMessage(AnimatorMessageAdapter aMsg)
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.err.println(this + "- exception: "+e);
    }
  }

  public void processMessage(UniversalMessageAdapter aMsg)
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.err.println(this + "- exception: "+e);
    }
  }
}
