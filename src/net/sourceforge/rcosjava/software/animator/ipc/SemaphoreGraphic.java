package net.sourceforge.rcosjava.software.animator.ipc;

import java.util.*;
import net.sourceforge.rcosjava.software.util.FIFOQueue;

public class SemaphoreGraphic
{
  private FIFOQueue attachedProcesses;
  private int value;

  public SemaphoreGraphic(int newProcess, int newValue)
  {
    attachedProcesses = new FIFOQueue(10,1);
    attachedProcesses.add(new Integer(newProcess));
    value = newValue;
  }

  public int removeFirstProcess()
  {
    attachedProcesses.goToHead();
    return ((Integer) attachedProcesses.retrieveCurrent()).intValue();
  }

  public void addProcess(int newProcess)
  {
    attachedProcesses.insert(new Integer(newProcess));
  }

  public int attachedProcesses()
  {
    return attachedProcesses.size();
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
