package org.rcosjava.software.animator.process;
import java.applet.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.util.*;
import org.rcosjava.messaging.messages.Message;
import org.rcosjava.software.animator.RCOSFrame;
import org.rcosjava.software.animator.support.GraphicButton;
import org.rcosjava.software.animator.support.NewLabel;
import org.rcosjava.software.animator.support.RCOSList;
import org.rcosjava.software.process.ProcessPriority;

/**
 * It is the interface which allows users to display the current process control
 * block of a selected process.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 10/1/97 Created. AN </DD>
 * <DD> 23/11/98 Converted to Java 1.1. AN </DD> </DT>
 * <P>
 * @author Andrew Newman
 * @created 14th January 1996
 * @version 1.00 $Date$
 */
public class ProcessControlBlockFrame extends RCOSFrame
{
  /**
   * Constructor for the ProcessManagerFrame object
   *
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param thisProcessManager Description of Parameter
   */
  public ProcessControlBlockFrame(int x, int y,
      ProcessManagerAnimator thisProcessManager)
  {
    setTitle("Process Control Block");
//    myProcessManager = thisProcessManager;
    setSize(x, y);
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
  }
}
