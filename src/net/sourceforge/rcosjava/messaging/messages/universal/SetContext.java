//******************************************************/
// FILE     : SetContextMessage.java
// PURPOSE  : Sets current CPU status of BP, SP, etc.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/01/1998   Created
//******************************************************/

package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.animator.cpu.CPUAnimator;
import net.sourceforge.rcosjava.hardware.cpu.Context;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

public class SetContext extends UniversalMessageAdapter
{
  private Context myContext;

  public SetContext(OSMessageHandler theSource,
    Context newContext)
  {
    super(theSource);
    myContext = (Context) newContext.clone();
  }

  public void doMessage(CPUAnimator theElement)
  {
    theElement.setContext(myContext);
  }
}

