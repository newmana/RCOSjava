package MessageSystem.Messages.OS;

import Software.Kernel.Kernel;
import Software.Interrupt.InterruptHandler;
import Software.Process.ProgramManager;
import Software.Terminal.SoftwareTerminal;
import Hardware.CPU.Interrupt;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class HandleInterrupt extends OSMessageAdapter
{
  private Interrupt intInterrupt;

  public HandleInterrupt(OSMessageHandler theSource,
    Interrupt intNewInterrupt)
  {
    super(theSource);
    intInterrupt = intNewInterrupt;
  }

  private void setInterrupt(Interrupt intNewInterrupt)
  {
    intInterrupt = intNewInterrupt;
  }

  public void doMessage(Kernel theElement)
  {
    theElement.handleInterrupt(intInterrupt);
  }

  public boolean undoableMessage()
  {
    if (intInterrupt.getType().equals("NewProcess"))
      return false;
    else
      return true;
  }
}

