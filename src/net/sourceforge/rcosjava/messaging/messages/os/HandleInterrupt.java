package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.interrupt.InterruptHandler;
import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.hardware.cpu.Interrupt;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

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

