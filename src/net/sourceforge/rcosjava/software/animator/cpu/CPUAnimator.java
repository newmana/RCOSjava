//***************************************************************************
// FILE     : CPUAnimator.java
// PACKAGE  : Animator.CPU
// PURPOSE  : Class used to animate CPU
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 24/01/96  Created
//            28/01/96  Modifying to become CPUAnimator
//            23/03/96  Moved into CPU package
//            02/02/97  Moved to Animator Package
//            03/02/97  Seperated between Message Handler and Frame.
//
//***************************************************************************/

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

  public void updateStack(Memory mNewStack)
  {
    cpuFrame.updateStack(mNewStack);
    cpuFrame.updateCode();
  }

  public void loadCode(Memory mCode)
  {
    try
    {
      cpuFrame.loadCode(mCode);
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
