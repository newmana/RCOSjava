package net.sourceforge.rcosjava.software.animator.ipc;

import java.util.*;
import net.sourceforge.rcosjava.software.util.FIFOQueue;

public class SemaphoreGraphic
{
  FIFOQueue attachedProcesses;
  int value;

  public SemaphoreGraphic(int newProcess, int newValue)
  {
    attachedProcesses = new FIFOQueue(10,0);
    attachedProcesses.add(new Integer(newProcess));
    value = newValue;
  }

  public int removeFirstProcess()
  {
    return ((Integer) attachedProcesses.retrieve()).intValue();
  }

  public void addProcess(int newProcess)
  {
    attachedProcesses.insert(new Integer(newProcess));
  }

  public Iterator getAttachedProcesses()
  {
    return attachedProcesses.iterator();
  }

  public void setValue(int newValue)
  {
    value = newValue;
  }

  public int getValue()
  {
    return value;
  }
}
