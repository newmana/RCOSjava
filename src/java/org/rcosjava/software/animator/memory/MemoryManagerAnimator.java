package org.rcosjava.software.animator.memory;

import java.awt.Component;
import javax.swing.ImageIcon;
import java.io.*;
import java.util.*;

import org.rcosjava.RCOS;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.software.animator.RCOSAnimator;
import org.rcosjava.software.animator.RCOSPanel;
import org.rcosjava.software.ipc.SharedMemory;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.memory.MemoryRequest;
import org.rcosjava.software.memory.MemoryReturn;

/**
 * Receives messages from MMU to display memory access i.e. reading and writing.
 * <P>
 * @author Andrew Newman.
 * @created 1st of August 2002
 * @version 1.00 $Date$
 */
public class MemoryManagerAnimator extends RCOSAnimator
{
  /**
   * Unique identifier to register to the animator post office.
   */
  private final static String MESSENGING_ID = "MemoryManagerAnimator";

  /**
   * Array that registers allocated and deallocated memory and other various
   * states that the memory object can be in.
   */
  private static MemoryState memoryState[] =
      new MemoryState[MemoryManager.MAX_PAGES];

  /**
   * The panel in which to display all of the results to.
   */
  private static MemoryManagerPanel panel;

  /**
   * Constructor for the MemoryManagerAnimator object
   *
   * @param postOffice Description of Parameter
   * @param images Description of Parameter
   */
  public MemoryManagerAnimator(AnimatorOffice postOffice, ImageIcon[] images)
  {
    super(MESSENGING_ID, postOffice);

    // Initialise memory state.
    for (int index = 0; index < memoryState.length; index++)
    {
      memoryState[index] = new MemoryState((byte) 0, 0, false);
    }

    panel = new MemoryManagerPanel(images, this);
  }

  /**
   * Setup the layout of the frame (menus, etc).
   *
   * @param c the parent component.
   */
  public void setupLayout(Component c)
  {
    panel.setupLayout(c);
  }

  /**
   * Returns the panel of this component.
   *
   * @return the panel of this component.
   */
  public RCOSPanel getPanel()
  {
    return panel;
  }

  /**
   * Sets memory state to allacted and the associated PID and memory type.
   * Refreshes the graphical representation of them.
   *
   * @param returnedMemory the object representing the allocated memory.
   */
  public void allocatedPages(MemoryReturn returnedMemory)
  {
    ArrayList list = new ArrayList();
    for (int count = 0; count < returnedMemory.getSize(); count++)
    {
      int index = returnedMemory.getPage(count);
      memoryState[index].allocated();
      memoryState[index].setPID(returnedMemory.getPID());
      memoryState[index].setMemoryType(returnedMemory.getMemoryType());
      list.add(new Integer(index));
    }
    panel.allocatedPages(list);
    panel.setPID(returnedMemory.getPID(), list);
  }

  /**
   * Sets memory state to unallacted and PID to 0.  Refreshes the graphical
   * representation of them.
   *
   * @param returnedMemory the object representing the deallocated memory.
   */
  public void deallocatedPages(MemoryReturn returnedMemory)
  {
    ArrayList list = new ArrayList();
    for (int count = 0; count < returnedMemory.getSize(); count++)
    {
      int index = returnedMemory.getPage(count);
      memoryState[index].unallocated();
      memoryState[index].setPID(0);
      list.add(new Integer(index));
    }
    panel.unallocatedPages(list);
    panel.setPID(0, list);
  }

  /**
   * Calls readingMemory on the associated frame.
   *
   * @param requestedMemory the data structure with the process id and the type
   *      of memory.
   */
  public void readingMemory(MemoryRequest requestedMemory)
  {
    List list = getMemoryIndices(requestedMemory.getPID(),
        requestedMemory.getMemoryType());
    for (int count = 0; count < list.size(); count++)
    {
      int index = ((Integer) list.get(count)).intValue();
      memoryState[index].beingRead();
    }
    panel.readingMemory(list);
  }

  /**
   * Calls writingMemory on the associated frame.
   *
   * @param requestedMemory Description of Parameter
   */
  public void writingMemory(MemoryRequest requestedMemory)
  {
    List list = getMemoryIndices(requestedMemory.getPID(),
        requestedMemory.getMemoryType());
    for (int count = 0; count < list.size(); count++)
    {
      int index = ((Integer) list.get(count)).intValue();
      memoryState[index].beingWritten();
    }
    panel.writingMemory(list);
  }

  /**
   * Calls finishedReadingMemory on the associated frame.
   *
   * @param requestedMemory the data structure containing the process id and the
   *      type of memory that has just been read.
   */
  public void finishedReadingMemory(MemoryRequest requestedMemory)
  {
    List list = getMemoryIndices(requestedMemory.getPID(),
        requestedMemory.getMemoryType());
    for (int count = 0; count < list.size(); count++)
    {
      int index = ((Integer) list.get(count)).intValue();
      memoryState[index].finishedBeingRead();
    }
    panel.finishedReadingMemory(list);
  }

  /**
   * Calls finishedWritingMemory on the associated frame.
   *
   * @param requestedMemory the data structure containing the process id and the
   *      type of memory that is being written to.
   */
  public void finishedWritingMemory(MemoryRequest requestedMemory)
  {
    List list = getMemoryIndices(requestedMemory.getPID(),
        requestedMemory.getMemoryType());
    for (int count = 0; count < list.size(); count++)
    {
      int index = ((Integer) list.get(count)).intValue();
      memoryState[index].finishedBeingWritten();
    }
    panel.finishedWritingMemory(list);
  }

  /**
   * Returns an array of an set of offsets of a certain PID and type.
   *
   * @param pid the process id belonging to the memory.
   * @param memoryType the memory type.
   * @return List the integer offsets.
   */
  List getMemoryIndices(int pid, byte memoryType)
  {
    ArrayList offsets = new ArrayList();
    for (int count = 0; count < MemoryManager.MAX_PAGES; count++)
    {
      if ((memoryState[count].getPID() == pid) &&
          (memoryState[count].getMemoryType() == memoryType))
      {
        offsets.add(new Integer(count));
      }
    }
    return offsets;
  }

  /**
   * Handle the serialization of the contents.
   */
  private void writeObject(ObjectOutputStream os) throws IOException
  {
    os.writeInt(memoryState.length);
    for (int index = 0; index < memoryState.length; index++)
    {
      os.writeObject(memoryState[index]);
    }
  }

  /**
   * Handle deserialization of the contents.  Ensures non-serializable
   * components correctly created.
   *
   * @param is stream that is being read.
   */
  private void readObject(ObjectInputStream is) throws IOException,
      ClassNotFoundException
  {
    register(MESSENGING_ID, RCOS.getAnimatorPostOffice());

//    panel = (MemoryManagerPanel) RCOS.getMemoryManagerAnimator().getPanel();
//
//    int numberMemoryStates = is.readInt();
//    memoryState = new MemoryState[numberMemoryStates];
//
//    for (int index = 0; index < numberMemoryStates; index++)
//    {
//      memoryState[index] = (MemoryState) is.readObject();
//    }
  }
}