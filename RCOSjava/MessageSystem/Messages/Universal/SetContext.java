//******************************************************/
// FILE     : SetContextMessage.java
// PURPOSE  : Sets current CPU status of BP, SP, etc.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/01/1998   Created
//******************************************************/

package MessageSystem.Messages.Universal;

import Software.Animator.CPU.CPUAnimator;
import Hardware.CPU.Context;
import MessageSystem.PostOffices.OS.OSMessageHandler;

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

