package org.rcosjava.software.animator.memory;

import java.awt.*;
import javax.swing.ImageIcon;
import java.io.*;
import java.util.*;

import org.rcosjava.RCOS;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.software.animator.RCOSAnimator;
import org.rcosjava.software.animator.RCOSPanel;
import org.rcosjava.software.ipc.SharedMemory;
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
   * Array that register allocated and deallocated memory.
   */
  private static ArrayList allocatedList = new ArrayList();

  /**
   * Array that registers reading and writing memory.
   */
  private static ArrayList readingWritingList = new ArrayList();

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
   * Calls allocatedPages on the associated frame.
   *
   * @param returnedMemory the object representing the allocated memory.
   */
  public void allocatedPages(MemoryReturn returnedMemory)
  {
    for (int count = 0; count < returnedMemory.getSize(); count++)
    {
      allocatedList.add(returnedMemory.getPage(count), new Boolean(true));
    }
    panel.allocatedPages(returnedMemory);
  }

  /**
   * Calls deallocatedPages on the associated frame.
   *
   * @param returnedMemory the object representing the deallocated memory.
   */
  public void deallocatedPages(MemoryReturn returnedMemory)
  {
    for (int count = 0; count < returnedMemory.getSize(); count++)
    {
      allocatedList.add(returnedMemory.getPage(count), new Boolean(false));
    }
    panel.deallocatedPages(returnedMemory);
  }

  /**
   * Calls readingMemory on the associated frame.
   *
   * @param requestedMemory the data structure with the process id and the type
   *      of memory.
   */
  public void readingMemory(MemoryRequest requestedMemory)
  {
    panel.readingMemory(requestedMemory.getPID(),
        requestedMemory.getMemoryType());
  }

  /**
   * Calls writingMemory on the associated frame.
   *
   * @param requestedMemory Description of Parameter
   */
  public void writingMemory(MemoryRequest requestedMemory)
  {
    panel.writingMemory(requestedMemory.getPID(),
        requestedMemory.getMemoryType());
  }

  /**
   * Calls finishedReadingMemory on the associated frame.
   *
   * @param requestedMemory the data structure containing the process id and the
   *      type of memory that has just been read.
   */
  public void finishedReadingMemory(MemoryRequest requestedMemory)
  {
    panel.finishedReadingMemory(requestedMemory.getPID(),
        requestedMemory.getMemoryType());
  }

  /**
   * Calls finishedWritingMemory on the associated frame.
   *
   * @param requestedMemory the data structure containing the process id and the
   *      type of memory that is being written to.
   */
  public void finishedWritingMemory(MemoryRequest requestedMemory)
  {
    panel.finishedWritingMemory(requestedMemory.getPID(),
        requestedMemory.getMemoryType());
  }

  /**
   * Handle the serialization of the contents.
   */
  private void writeObject(ObjectOutputStream os) throws IOException
  {

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
  }
}