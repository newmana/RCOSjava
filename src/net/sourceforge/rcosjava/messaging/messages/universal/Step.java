//***************************************************************************
// FILE    : StepMessage.java
// PACKAGE :
// PURPOSE :
// AUTHOR  : Andrew Newman
// MODIFIED:
// HISTORY : 28/03/96  Created
//
//
//***************************************************************************

package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.software.animator.process.ProcessManagerAnimator;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;

public class Step extends UniversalMessageAdapter
{
  public Step(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  public void doMessage(ProgramManager theElement)
  {
    theElement.stepThread();
  }
}

